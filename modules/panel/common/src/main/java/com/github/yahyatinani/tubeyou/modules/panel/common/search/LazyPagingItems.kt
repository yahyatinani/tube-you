package com.github.yahyatinani.tubeyou.modules.panel.common.search

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.CombinedLoadStates
import androidx.paging.DifferCallback
import androidx.paging.ItemSnapshotList
import androidx.paging.LOGGER
import androidx.paging.LOG_TAG
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Logger
import androidx.paging.NullPaddedList
import androidx.paging.PagingData
import androidx.paging.PagingDataDiffer
import androidx.paging.compose.LazyPagingItems
import io.github.yahyatinani.recompose.clearEvent
import io.github.yahyatinani.recompose.clearFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.recompose.regFx
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

private val IncompleteLoadState = LoadState.NotLoading(false)
private val InitialLoadStates = LoadStates(
  LoadState.Loading,
  IncompleteLoadState,
  IncompleteLoadState
)

@SuppressLint("RestrictedApi")
class LazyPagingItems2<T : Any> internal constructor(
  /**
   * the [Flow] object which contains a stream of [PagingData] elements.
   */
  private val flow: Flow<PagingData<T>>,
  val onSuccessEvent: Event,
  val onAppendEvent: Event
) {
  private val triggerAppending =
    hashCode() + onSuccessEvent.hashCode() + onAppendEvent.hashCode()

  init {
    regFx(triggerAppending) { index ->
      // Notify Paging of the item access to trigger any loads necessary to
      // fulfill prefetchDistance.
      pagingDataDiffer[index as Int]
    }

    regEventFx(triggerAppending) { _, event ->
      m(fx to v(event))
    }
  }

  fun clear() {
    clearEvent(triggerAppending)
    clearFx(triggerAppending)
  }

  private val mainDispatcher = Dispatchers.Main

  /**
   * Contains the immutable [ItemSnapshotList] of currently presented items, including any
   * placeholders if they are enabled.
   * Note that similarly to [peek] accessing the items in a list will not trigger any loads.
   * Use [get] to achieve such behavior.
   */
  var itemSnapshotList by mutableStateOf(ItemSnapshotList<T>(0, 0, emptyList()))
    private set

  /**
   * The number of items which can be accessed.
   */
  val itemCount: Int get() = itemSnapshotList.size

  private val differCallback: DifferCallback = object : DifferCallback {
    override fun onChanged(position: Int, count: Int) {
      if (count > 0) {
        updateItemSnapshotList()
      }
    }

    override fun onInserted(position: Int, count: Int) {
      if (count > 0) {
        updateItemSnapshotList()
      }
    }

    override fun onRemoved(position: Int, count: Int) {
      if (count > 0) {
        updateItemSnapshotList()
      }
    }
  }

  private val pagingDataDiffer: PagingDataDiffer<T> =
    object : PagingDataDiffer<T>(
      differCallback = differCallback,
      mainContext = mainDispatcher
    ) {
      override suspend fun presentNewList(
        previousList: NullPaddedList<T>,
        newList: NullPaddedList<T>,
        lastAccessedIndex: Int,
        onListPresentable: () -> Unit
      ): Int? {
        onListPresentable()
        updateItemSnapshotList()
        return null
      }
    }

  private fun updateItemSnapshotList() {
    itemSnapshotList = pagingDataDiffer.snapshot()

    val event = onSuccessEvent.conj(
      v(
        triggerAppending,
        itemSnapshotList.fold(v<T>()) { acc, t ->
          if (t != null) acc.conj(t) else acc
        }
      )
    )
    dispatch(event)
  }

  /**
   * Retry any failed load requests that would result in a [LoadState.Error] update to this
   * [LazyPagingItems].
   *
   * Unlike [refresh], this does not invalidate [PagingSource], it only retries failed loads
   * within the same generation of [PagingData].
   *
   * [LoadState.Error] can be generated from two types of load requests:
   *  * [PagingSource.load] returning [PagingSource.LoadResult.Error]
   *  * [RemoteMediator.load] returning [RemoteMediator.MediatorResult.Error]
   */
  fun retry() {
    pagingDataDiffer.retry()
  }

  /**
   * Refresh the data presented by this [LazyPagingItems].
   *
   * [refresh] triggers the creation of a new [PagingData] with a new instance of [PagingSource]
   * to represent an updated snapshot of the backing dataset. If a [RemoteMediator] is set,
   * calling [refresh] will also trigger a call to [RemoteMediator.load] with [LoadType] [REFRESH]
   * to allow [RemoteMediator] to check for updates to the dataset backing [PagingSource].
   *
   * Note: This API is intended for UI-driven refresh signals, such as swipe-to-refresh.
   * Invalidation due repository-layer signals, such as DB-updates, should instead use
   * [PagingSource.invalidate].
   *
   * @see PagingSource.invalidate
   */
  fun refresh() {
    pagingDataDiffer.refresh()
  }

  /**
   * A [CombinedLoadStates] object which represents the current loading state.
   */
  var loadState: CombinedLoadStates by mutableStateOf(
    pagingDataDiffer.loadStateFlow.value
      ?: CombinedLoadStates(
        refresh = InitialLoadStates.refresh,
        prepend = InitialLoadStates.prepend,
        append = InitialLoadStates.append,
        source = InitialLoadStates
      )
  )
    private set

  internal suspend fun collectLoadState() {
    pagingDataDiffer.loadStateFlow.filterNotNull().collect {
      if (it.append == LoadState.Loading) {
        dispatch(onAppendEvent.conj(it))
      }
      loadState = it
    }
  }

  internal suspend fun collectPagingData() {
    flow.collectLatest {
      pagingDataDiffer.collectFrom(it)
    }
  }

  private companion object {
    init {
      /**
       * Implements the Logger interface from paging-common and injects it into the LOGGER
       * global var stored within Pager.
       *
       * Checks for null LOGGER because other runtime entry points to paging can also
       * inject a Logger
       */
      LOGGER = LOGGER ?: object : Logger {
        override fun isLoggable(level: Int): Boolean {
          return Log.isLoggable(LOG_TAG, level)
        }

        override fun log(level: Int, message: String, tr: Throwable?) {
          when {
            tr != null && level == Log.DEBUG -> Log.d(LOG_TAG, message, tr)
            tr != null && level == Log.VERBOSE -> Log.v(LOG_TAG, message, tr)
            level == Log.DEBUG -> Log.d(LOG_TAG, message)
            level == Log.VERBOSE -> Log.v(LOG_TAG, message)
            else -> {
              throw IllegalArgumentException(
                "debug level $level is requested but Paging only supports " +
                  "default logging for level 2 (DEBUG) or level 3 (VERBOSE)"
              )
            }
          }
        }
      }
    }
  }
}

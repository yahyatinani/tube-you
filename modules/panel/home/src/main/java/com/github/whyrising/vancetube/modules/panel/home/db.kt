package com.github.whyrising.vancetube.modules.panel.home

import androidx.compose.runtime.Immutable
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.ids.coeffects
import com.github.whyrising.recompose.ids.recompose
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import kotlinx.serialization.Serializable

typealias AppDb = IPersistentMap<Any, Any>

/**
 * Spec
 *
 * e.g:
 * {:state [States.Loading]
 * :popular_vids ([VideoData])}
 */

@Serializable
data class ThumbnailData(val url: String)

@Immutable
@Serializable
data class VideoData(
  val videoId: String,
  val title: String,
  val videoThumbnails: List<ThumbnailData>,
  val lengthSeconds: Int,
  val author: String,
  val authorId: String,
  val viewCount: Long,
  val publishedText: String
)

// -- Home FSM -----------------------------------------------------------------
enum class States { Loading, Refreshing, Loaded, Failed }

val homeStateMachine = m<Any?, Any>(
  null to m(common.initialise to States.Loading),
  States.Loading to m(
    home.set_popular_vids to States.Loaded,
    ":error" to States.Failed
  ),
  States.Loaded to m(
    home.refresh to States.Refreshing,
    home.load_popular_videos to States.Loaded
  ),
  States.Refreshing to m(
    home.set_popular_vids to States.Loaded,
    ":error" to States.Failed
  ),
  States.Failed to m(home.load_popular_videos to States.Loading)
)

fun nextState(
  fsm: Map<Any?, Any>,
  currentState: States?,
  transition: Any
): Any? = getIn(fsm, l(currentState, transition))

fun homeCurrentState(appDb: AppDb) =
  getIn<States>(appDb, l(home.panel, home.state))

fun updateToNextState(db: AppDb, event: Any): AppDb {
  val currentState = homeCurrentState(db)
  val nextState = nextState(homeStateMachine, currentState, event)
  return if (nextState != null) {
    assocIn(db, l(home.panel, home.state), nextState) as AppDb
  } else db
}

fun handleNextState(db: AppDb, event: Event): AppDb = event.let { (id) ->
  updateToNextState(db, id)
}

// -- cofx Registrations -------------------------------------------------------
val regCofx by lazy {
  regCofx(home.fsm) { cofx ->
    val (eventId) = cofx[coeffects.originalEvent] as PersistentVector<Any>
    val nextDb = updateToNextState(getAppDb(cofx), eventId)
    cofx.assoc(recompose.db, nextDb)
  }
}

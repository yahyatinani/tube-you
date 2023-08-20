package io.github.yahyatinani.tubeyou.ui.modules.feature.search.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.core.keywords.States
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.core.ui.ChannelItem
import io.github.yahyatinani.tubeyou.core.ui.Panel
import io.github.yahyatinani.tubeyou.core.ui.PlayListLandscape
import io.github.yahyatinani.tubeyou.core.ui.PlayListPortrait
import io.github.yahyatinani.tubeyou.core.ui.VideoItemLandscapeCompact
import io.github.yahyatinani.tubeyou.core.ui.VideoItemPortrait
import io.github.yahyatinani.tubeyou.core.viewmodels.ChannelVm
import io.github.yahyatinani.tubeyou.core.viewmodels.PlaylistVm
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v

@Composable
fun PortraitListItem(
  vm: Any,
  index: Int,
  lastIndex: Int,
  videoInfoTextStyle: TextStyle
) {
  when (vm) {
    is VideoVm -> {
      VideoItemPortrait(
        viewModel = vm,
        videoInfoTextStyle = videoInfoTextStyle,
        onClick = { dispatch(v("stream_panel_fsm", common.play_video, vm)) }
      )
    }

    is ChannelVm -> {
      if (index != 0) Divider(color = Color.DarkGray)

      ChannelItem(
        modifier = Modifier
          .clickable { /*TODO*/ }
          .fillMaxWidth(),
        vm = vm
      )

      if (index != lastIndex) Divider(color = Color.DarkGray)
    }

    else -> PlayListPortrait(
      modifier = Modifier.padding(start = 12.dp),
      viewModel = vm as PlaylistVm,
      videoInfoTextStyle = videoInfoTextStyle
    )
  }
}

@Composable
fun LandscapeListItem(vm: Any) {
  when (vm) {
    is VideoVm -> VideoItemLandscapeCompact(
      viewModel = vm,
      onClick = { /*TODO*/ }
    )

    is ChannelVm -> ChannelItem(
      modifier = Modifier
        .clickable { /*TODO*/ }
        .fillMaxWidth(),
      vm = vm,
      avatarPaddingValues = PaddingValues(horizontal = 24.dp)
    )

    else -> PlayListLandscape(
      viewModel = vm as PlaylistVm,
      onClick = { /*TODO*/ }
    )
  }
}

@Composable
internal fun SearchScreen(
  uiState: UIState,
  orientation: Int
) {
  val data = uiState.data
  val state: States = get<States>(data, common.state)!!

  Panel(state = state) { appendLoader ->
    if (state == States.FAILED) {
      val error: Int? = get<Int>(data, common.error)
      Text(text = "Request failed! Error: $error")

      return@Panel
    }

    val searchResults: UIState = get<UIState>(data, searchBar.results)!!
    val isPortraitMode = orientation == Configuration.ORIENTATION_PORTRAIT
    LazyColumn(
      modifier = Modifier
        .testTag("search_results_list")
        .fillMaxSize(),
      state = rememberLazyListState()
    ) {
      val videoInfoTextStyle = TextStyle.Default.copy(fontSize = 12.sp)
      val items = searchResults.data as List<Any>
      itemsIndexed(
        items = items
      ) { index: Int, vm: Any ->
        dispatch(v("append_search_results", index))
        when {
          isPortraitMode -> {
            PortraitListItem(
              vm = vm,
              index = index,
              lastIndex = items.size,
              videoInfoTextStyle = videoInfoTextStyle
            )
          }

          else -> {
            LandscapeListItem(vm)
          }
        }
      }
      item { appendLoader() }
    }
  }
}

@Composable
fun SearchRoute(
  uiState: UIState = watch(
    query = v(search.view_model, LocalContext.current.resources)
  ),
  orientation: Int
) {
  SearchScreen(
    uiState = uiState,
    orientation = orientation
  )
}

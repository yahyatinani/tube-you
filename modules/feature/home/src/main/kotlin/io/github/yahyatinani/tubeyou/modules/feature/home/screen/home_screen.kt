package io.github.yahyatinani.tubeyou.modules.feature.home.screen

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.yahyatinani.tubeyou.modules.core.keywords.States
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.core.ui.PullRefreshPanel
import io.github.yahyatinani.tubeyou.core.ui.VideosGrid
import io.github.yahyatinani.tubeyou.core.ui.VideosList
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.modules.feature.home.fx.RegScrollUpFx
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v

@Composable
internal fun HomeScreen(
  state: States,
  homeVideos: UIState,
  error: Int?,
  orientation: Int,
  isCompact: Boolean,
  onRefresh: () -> Unit,
  onClickVideo: (VideoVm) -> Unit
) {
  PullRefreshPanel(
    state = state,
    error = error,
    onRefresh = onRefresh
  ) {
    if (isCompact) {
      val listState = rememberLazyListState()
      RegScrollUpFx {
        listState.animateScrollToItem(index = 0)
      }
      VideosList(
        orientation = orientation,
        listState = listState,
        videos = homeVideos,
        onClickVideo = onClickVideo
      )
    } else {
      val gridState = rememberLazyGridState()
      RegScrollUpFx {
        gridState.animateScrollToItem(index = 0)
      }

      VideosGrid(
        orientation = orientation,
        gridState = gridState,
        videos = homeVideos,
        onClickVideo = onClickVideo
      )
    }
  }
}

@Composable
fun HomeRoute(
  uiState: UIState = watch(v(home.view_model, LocalContext.current.resources)),
  orientation: Int,
  isCompact: Boolean,
  onPullRefresh: () -> Unit,
  onClickVideo: (VideoVm) -> Unit
) {
  val data = uiState.data
  val state: States = get<States>(data, common.state)!!
  val homeVideos: UIState = get<UIState>(data, home.content)!!
  val error: Int? = get<Int>(data, common.error)
  HomeScreen(
    state = state,
    homeVideos = homeVideos,
    error = error,
    orientation = orientation,
    isCompact = isCompact,
    onRefresh = onPullRefresh,
    onClickVideo = onClickVideo
  )
}

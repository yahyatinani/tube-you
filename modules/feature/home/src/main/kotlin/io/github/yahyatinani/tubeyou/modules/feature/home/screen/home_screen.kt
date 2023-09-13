package io.github.yahyatinani.tubeyou.modules.feature.home.screen

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.github.yahyatinani.tubeyou.modules.core.keywords.States
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.httpfx.HttpError
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.core.ui.PullRefreshPanel
import io.github.yahyatinani.tubeyou.core.ui.VideosGrid
import io.github.yahyatinani.tubeyou.core.ui.VideosListCompact
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.modules.feature.home.fx.RegScrollUpFx
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v

@Composable
internal fun HomeScreen(
  uiState: UIState,
  orientation: Int,
  isCompact: Boolean,
  onRefresh: () -> Unit,
  onClickVideo: (VideoVm) -> Unit
) {
  val data = uiState.data
  val state: States = get<States>(data, common.state)!!
  PullRefreshPanel(
    state = state,
    onRefresh = onRefresh
  ) {
    if (state == States.FAILED) {
      val error: HttpError? = get<HttpError>(data, common.error)
      Text(text = "Request failed! status code: ${error?.status}")
    }

    val homeVideos: UIState = get<UIState>(data, home.content)!!
    val videosListTestTag = "home_videos_list"
    if (isCompact) {
      val listState = rememberLazyListState()
      RegScrollUpFx {
        listState.animateScrollToItem(index = 0)
      }
      VideosListCompact(
        modifier = Modifier.testTag(videosListTestTag),
        videos = homeVideos,
        orientation = orientation,
        listState = listState,
        onClickVideo = onClickVideo
      )
    } else {
      val gridState = rememberLazyGridState()
      RegScrollUpFx {
        gridState.animateScrollToItem(index = 0)
      }

      VideosGrid(
        modifier = Modifier.testTag(videosListTestTag),
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
  HomeScreen(
    uiState = uiState,
    orientation = orientation,
    isCompact = isCompact,
    onRefresh = onPullRefresh,
    onClickVideo = onClickVideo
  )
}

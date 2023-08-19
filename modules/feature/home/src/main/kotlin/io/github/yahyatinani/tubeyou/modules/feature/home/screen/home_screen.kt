package io.github.yahyatinani.tubeyou.modules.feature.home.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.core.ui.PullRefreshPanel
import io.github.yahyatinani.tubeyou.core.ui.VideoItemPortrait
import io.github.yahyatinani.tubeyou.core.ui.VideosList
import io.github.yahyatinani.tubeyou.core.viewmodels.PanelVm
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoViewModel
import io.github.yahyatinani.tubeyou.core.viewmodels.Videos
import io.github.yahyatinani.tubeyou.modules.feature.home.cofx.RetRegHomeCofx
import io.github.yahyatinani.tubeyou.modules.feature.home.events.RegHomeEvents
import io.github.yahyatinani.tubeyou.modules.feature.home.fx.RegScrollUpFx
import io.github.yahyatinani.tubeyou.modules.feature.home.subs.RegHomeSubs
import io.github.yahyatinani.y.core.v

@Composable
internal fun InitHome() {
  RegHomeSubs()
  RetRegHomeCofx()
  RegHomeEvents()
  LaunchedEffect(Unit) {
    dispatch(v(home.fsm, home.load))
  }
}

@Composable
internal fun HomeScreen(
  panelVm: PanelVm,
  orientation: Int,
  thumbnailHeight: Dp
) {
  PullRefreshPanel(
    panelVm = panelVm,
    onRefresh = { dispatch(v(home.fsm, home.refresh)) }
  ) { videos ->
    val listState = rememberLazyListState()
    RegScrollUpFx {
      listState.animateScrollToItem(index = 0)
    }

    VideosList(
      orientation = orientation,
      listState = listState,
      videos = videos,
      thumbnailHeight = thumbnailHeight
    ) {
      dispatch(v("stream_panel_fsm", common.play_video, it))
    }
  }
}

@Composable
fun VideosGrid(
  orientation: Int = 1,
  gridState: LazyGridState,
  videos: Videos,
  thumbnailHeight: Dp
) {
  LazyVerticalGrid(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(40.dp),
    state = gridState,
    columns = GridCells.Fixed(
      count = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
    ),
    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
    modifier = Modifier
      .testTag("popular_videos_list")
      .padding(start = 16.dp, end = 16.dp)
  ) {
    items(
      items = videos.value as List<VideoViewModel>,
      key = { it.id }
    ) { viewModel ->
      VideoItemPortrait(
        modifier = Modifier
          .testTag("video")
          .padding(bottom = 0.dp),
        videoInfoTextStyle = TextStyle.Default.copy(fontSize = 14.sp),
        viewModel = viewModel,
        thumbnailHeight = thumbnailHeight
      )
    }
  }
}

@Composable
fun HomeRoute(orientation: Int, thumbnailHeight: Dp) {
  InitHome()

  val panelVm =
    watch<PanelVm>(v(home.view_model, LocalContext.current.resources))

  HomeScreen(
    panelVm = panelVm,
    orientation = orientation,
    thumbnailHeight = thumbnailHeight
  )
}

package io.github.yahyatinani.tubeyou.modules.feature.home.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.core.ui.PullRefreshPanel
import io.github.yahyatinani.tubeyou.core.ui.VideoItemPortrait
import io.github.yahyatinani.tubeyou.core.ui.VideosList
import io.github.yahyatinani.tubeyou.core.viewmodels.PanelVm
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoViewModel
import io.github.yahyatinani.tubeyou.core.viewmodels.Videos
import io.github.yahyatinani.tubeyou.modules.feature.home.fx.RegScrollUpFx
import io.github.yahyatinani.y.core.v

@Composable
fun VideosGrid(
  orientation: Int = 1,
  gridState: LazyGridState,
  videos: Videos,
  onClickVideo: (VideoViewModel) -> Unit
) {
  LazyVerticalGrid(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(40.dp),
    state = gridState,
    columns = GridCells.Fixed(
      count = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
    ),
    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
  ) {
    items(
      items = videos.value as List<VideoViewModel>,
      key = { it.id }
    ) { viewModel ->
      VideoItemPortrait(
        modifier = Modifier.padding(bottom = 0.dp),
        videoInfoTextStyle = TextStyle.Default.copy(fontSize = 14.sp),
        viewModel = viewModel,
        onClick = { onClickVideo(viewModel) }
      )
    }
  }
}

@Composable
internal fun HomeScreen(
  panelVm: PanelVm,
  orientation: Int,
  isCompact: Boolean,
  onRefresh: () -> Unit,
  onClickVideo: (VideoViewModel) -> Unit
) {
  PullRefreshPanel(
    panelVm = panelVm,
    onRefresh = onRefresh
  ) { videos ->
    if (isCompact) {
      val listState = rememberLazyListState()
      RegScrollUpFx {
        listState.animateScrollToItem(index = 0)
      }
      VideosList(
        orientation = orientation,
        listState = listState,
        videos = videos,
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
        videos = videos,
        onClickVideo = onClickVideo
      )
    }
  }
}

@Composable
fun HomeRoute(
  panelVm: PanelVm = watch(v(home.view_model, LocalContext.current.resources)),
  orientation: Int,
  isCompact: Boolean,
  onPullRefresh: () -> Unit,
  onClickVideo: (VideoViewModel) -> Unit
) {
  HomeScreen(
    panelVm = panelVm,
    orientation = orientation,
    isCompact = isCompact,
    onRefresh = onPullRefresh,
    onClickVideo = onClickVideo
  )
}

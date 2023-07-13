package com.github.yahyatinani.tubeyou.modules.designsystem.component

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos

@Composable
fun VideosList(
  orientation: Int = 1,
  listState: LazyListState,
  videos: Videos,
  thumbnailHeight: Dp,
  playVideo: (VideoViewModel) -> Unit = {}
) {
  val isPortrait = orientation == ORIENTATION_PORTRAIT
  LazyColumn(
    state = listState,
    modifier = Modifier
      .testTag("popular_videos_list")
      .fillMaxSize()
      .then(if (isPortrait) Modifier else Modifier.padding(horizontal = 16.dp))
  ) {
    items(
      items = videos.value as List<VideoViewModel>,
      key = { it.id }
    ) { viewModel ->
      when {
        isPortrait -> {
          VideoItemPortrait(
            viewModel = viewModel,
            thumbnailHeight = thumbnailHeight,
            play = { playVideo(viewModel) }
          )
        }

        else -> VideoItemLandscapeCompact(
          viewModel = viewModel,
          thumbnailHeight = thumbnailHeight
        )
      }
    }
  }
}

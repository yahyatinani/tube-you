package com.github.whyrising.vancetube.modules.designsystem.component

import android.content.res.Configuration
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
import com.github.whyrising.vancetube.modules.designsystem.data.Videos

@Composable
fun VideosList(
  orientation: Int = 1,
  listState: LazyListState,
  videos: Videos,
  thumbnailHeight: Dp
) {
  LazyColumn(
    state = listState,
    modifier = Modifier
      .testTag("popular_videos_list")
      .fillMaxSize()
      .then(
        if (orientation == Configuration.ORIENTATION_PORTRAIT) Modifier
        else Modifier.padding(horizontal = 16.dp)
      )
  ) {
    items(
      items = videos.value,
      key = { it.id }
    ) { viewModel ->
      when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> VideoItemPortrait(
          viewModel = viewModel,
          thumbnailHeight = thumbnailHeight
        )

        else -> VideoItemLandscapeCompact(
          viewModel = viewModel,
          thumbnailHeight = thumbnailHeight
        )
      }
    }
  }
}

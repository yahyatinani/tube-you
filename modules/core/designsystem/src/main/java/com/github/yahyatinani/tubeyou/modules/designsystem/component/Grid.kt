package com.github.yahyatinani.tubeyou.modules.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos

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
      items = videos.value,
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

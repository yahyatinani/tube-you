package io.github.yahyatinani.tubeyou.core.ui

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm

@Composable
fun VideosGrid(
  orientation: Int,
  gridState: LazyGridState,
  videos: UIState,
  onClickVideo: (VideoVm) -> Unit
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
      items = videos.data as List<VideoVm>,
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

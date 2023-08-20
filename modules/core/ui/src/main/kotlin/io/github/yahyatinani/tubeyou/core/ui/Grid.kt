package io.github.yahyatinani.tubeyou.core.ui

import android.content.res.Configuration.ORIENTATION_PORTRAIT
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
  modifier: Modifier = Modifier,
  orientation: Int,
  gridState: LazyGridState,
  videos: UIState,
  onClickVideo: (VideoVm) -> Unit
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(
      count = if (orientation == ORIENTATION_PORTRAIT) 2 else 3
    ),
    modifier = modifier.padding(start = 16.dp, end = 16.dp),
    state = gridState,
    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(40.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    val videoInfoTextStyle = TextStyle.Default.copy(fontSize = 14.sp)
    val itemModifier = Modifier.padding(bottom = 0.dp)
    items(
      items = videos.data as List<VideoVm>,
      key = { it.id }
    ) { viewModel ->
      VideoItemPortrait(
        modifier = itemModifier,
        videoInfoTextStyle = videoInfoTextStyle,
        viewModel = viewModel,
        onClick = { onClickVideo(viewModel) }
      )
    }
  }
}

package io.github.yahyatinani.tubeyou.core.ui

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.designsystem.component.LiveDurationText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ShortDurationText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.VideoDurationText
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.modules.core.designsystem.R

@Composable
fun VideosListCompact(
  modifier: Modifier = Modifier,
  videos: UIState,
  orientation: Int,
  listState: LazyListState,
  onClickVideo: (VideoVm) -> Unit
) {
  val isPortraitMode = orientation == ORIENTATION_PORTRAIT
  LazyColumn(
    state = listState,
    modifier = modifier
      .fillMaxSize()
      .let { if (isPortraitMode) it else it.padding(horizontal = 16.dp) }
  ) {
    val videoInfoTextStyle = TextStyle.Default.copy(fontSize = 12.sp)
    items(
      items = videos.data as List<VideoVm>,
      key = { it.id }
    ) { viewModel ->
      VideoItemCompact(
        viewModel = viewModel,
        videoInfoTextStyle = videoInfoTextStyle,
        isPortraitMode = isPortraitMode,
        onClickVideo = onClickVideo
      )
    }
  }
}

@Composable
fun ThumbnailContent(viewModel: VideoVm) = when {
  viewModel.isLiveStream -> LiveDurationText()
  viewModel.isShort -> ShortDurationText()
  else -> VideoDurationText(
    duration = when {
      viewModel.isUpcoming -> stringResource(R.string.upcoming)
      else -> viewModel.length
    }
  )
}

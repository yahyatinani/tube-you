package io.github.yahyatinani.tubeyou.core.ui

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.component.LiveDurationText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ShortDurationText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.VideoDurationText
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoViewModel
import io.github.yahyatinani.tubeyou.core.viewmodels.Videos
import io.github.yahyatinani.tubeyou.modules.core.designsystem.R

@Composable
fun VideosList(
  orientation: Int = 1,
  listState: LazyListState,
  videos: Videos,
  onClickVideo: (VideoViewModel) -> Unit = {}
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
            onClick = { onClickVideo(viewModel) }
          )
        }

        else -> {
          VideoItemLandscapeCompact(
            viewModel = viewModel,
            onClick = { onClickVideo(viewModel) }
          )
        }
      }
    }
  }
}

@Composable
fun ThumbnailContent(viewModel: VideoViewModel) = when {
  viewModel.isLiveStream -> LiveDurationText()
  viewModel.isShort -> ShortDurationText()
  else -> VideoDurationText(
    duration = when {
      viewModel.isUpcoming -> stringResource(R.string.upcoming)
      else -> viewModel.length
    }
  )
}

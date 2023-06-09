package com.github.yahyatinani.tubeyou.modules.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatVideoInfo
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue300
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun Panel(
  modifier: Modifier = Modifier,
  panelVm: PanelVm,
  content: @Composable (
    videos: Videos,
    triggerAppending: Any?,
    appendLoader: @Composable () -> Unit
  ) -> Unit
) {
  Box(modifier = modifier.fillMaxSize()) {
    content(panelVm.videos, panelVm.appendEvent) {
      if (panelVm.isAppending) {
        Box(modifier = Modifier.fillMaxWidth()) {
          CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = Blue300
          )
        }
      }
    }

    if (panelVm.isLoading) {
      // TODO: if online use placeholder UI loader.
      CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
        color = Blue300
      )
    }

    if (panelVm.error != null) {
      // TODO: Implement proper UI for errors. Also, make it an argument.
      Text(text = "Request failed! Error: ${panelVm.error}")
    }
  }
}

@Composable
fun PullRefreshPanel(
  modifier: Modifier = Modifier,
  panelVm: PanelVm,
  onRefresh: () -> Unit = {},
  content: @Composable (videos: Videos) -> Unit
) {
  Panel(modifier = modifier, panelVm = panelVm) { videos, _, _ ->
    SwipeRefresh(
      modifier = Modifier.testTag("swipe_refresh"),
      swipeEnabled = !panelVm.isLoading,
      state = rememberSwipeRefreshState(panelVm.isRefreshing),
      onRefresh = onRefresh,
      indicator = { state, refreshTrigger ->
        val colorScheme = MaterialTheme.colorScheme
        SwipeRefreshIndicator(
          state = state,
          refreshTriggerDistance = refreshTrigger,
          backgroundColor = colorScheme.primaryContainer,
          contentColor = colorScheme.onBackground,
          elevation = if (isSystemInDarkTheme()) 0.dp else 4.dp
        )
      }
    ) {
      content(videos)
    }
  }
}

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun HomePreview() {
  val viewModel = VideoViewModel(
    id = "#ldfj243kj2r",
    authorId = "2342lk2sdf",
    title = "Title",
    thumbnail = "",
    length = "2:23",
    info = formatVideoInfo(
      author = "Jon Deo",
      authorId = "2342lk2sdf",
      text1 = "32432",
      publishedText = "2 hours ago",
      text2 = "views"
    )
  )

  TyTheme {
    PullRefreshPanel(
      panelVm = PanelVm.Loaded(
        videos = Videos(v(viewModel, viewModel, viewModel, viewModel))
      )
    ) { videos ->
      VideosList(
        orientation = 1,
        listState = rememberLazyListState(),
        videos = videos,
        thumbnailHeight = rememberThumbnailHeightPortrait()
      )
    }
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeDarkPreview() {
  HomePreview()
}

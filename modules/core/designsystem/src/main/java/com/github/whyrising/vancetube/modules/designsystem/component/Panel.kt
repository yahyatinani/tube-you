package com.github.whyrising.vancetube.modules.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.github.whyrising.vancetube.modules.designsystem.core.formatVideoInfo
import com.github.whyrising.vancetube.modules.designsystem.data.VideoViewModel
import com.github.whyrising.vancetube.modules.designsystem.data.Videos
import com.github.whyrising.vancetube.modules.designsystem.data.VideosPanelVm
import com.github.whyrising.vancetube.modules.designsystem.theme.Blue300
import com.github.whyrising.vancetube.modules.designsystem.theme.VanceTheme
import com.github.whyrising.y.core.v
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun VideosPanel(
  modifier: Modifier = Modifier,
  state: VideosPanelVm,
  onRefresh: () -> Unit = {},
  content: @Composable (videos: Videos) -> Unit
) {
  Box(modifier = modifier.fillMaxSize()) {
    if (state.isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
        color = Blue300
      )
    }

    if (state.showList) {
      SwipeRefresh(
        modifier = Modifier.testTag("swipe_refresh"),
        state = rememberSwipeRefreshState(state.isRefreshing),
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
        content(state.videos)
      }
    } else if (state.error != null) {
      // TODO: Implement proper UI for errors.
      Text(text = "Request failed! Error: ${state.error}")
    }
  }
}

// -- Previews -----------------------------------------------------------------

private val designTimeData = v(
  VideoViewModel(
    "#ldfj243kj2r",
    "2342lk2sdf",
    "Title",
    "",
    "2:23",
    formatVideoInfo(
      author = "Jon Deo",
      authorId = "2342lk2sdf",
      viewCount = "32432",
      publishedText = "2 hours ago",
      viewsLabel = "views"
    )
  ),
  VideoViewModel(
    "#ld2lk43kj2r",
    "fklj223jflrk23j",
    "Very long tiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
      "iiiiiiiiiiiiiiiiiiiiitle",
    "",
    "2:23",
    formatVideoInfo(
      author = "Jon Deo",
      authorId = "2342lk2sdf",
      viewCount = "32432",
      publishedText = "2 hours ago",
      viewsLabel = "views"
    )
  )
)

@Preview(showBackground = true)
@Composable
fun HomePreview() {
  VanceTheme {
    VideosPanel(
      state = VideosPanelVm(
        videos = Videos(designTimeData),
        showList = true
      )
    ) { videos ->
      VideosList(
        orientation = 1,
        listState = rememberLazyListState(),
        videos = videos
      )
    }
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeDarkPreview() {
  HomePreview()
}

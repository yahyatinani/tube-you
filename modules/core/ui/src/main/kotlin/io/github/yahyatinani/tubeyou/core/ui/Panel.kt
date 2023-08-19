package io.github.yahyatinani.tubeyou.core.ui

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AppendingLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue300
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.yahyatinani.tubeyou.core.viewmodels.PanelVm
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoViewModel
import io.github.yahyatinani.tubeyou.core.viewmodels.Videos
import io.github.yahyatinani.y.core.v

@Composable
fun Panel(
  modifier: Modifier = Modifier,
  panelVm: PanelVm,
  content: @Composable (
    videos: Videos,
    appendLoader: @Composable () -> Unit
  ) -> Unit
) {
  Box(modifier = modifier.fillMaxSize()) {
    content(panelVm.videos) {
      if (panelVm.isAppending) {
        AppendingLoader()
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
  Panel(modifier = modifier, panelVm = panelVm) { videos, _ ->
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
    info = AnnotatedString("Jon Deo")
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

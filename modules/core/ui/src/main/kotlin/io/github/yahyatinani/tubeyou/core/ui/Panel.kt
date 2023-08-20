package io.github.yahyatinani.tubeyou.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.core.keywords.States
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.APPENDING
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.LOADING
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.REFRESHING
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AppendingLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue300
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun Panel(
  modifier: Modifier = Modifier,
  state: States,
  content: @Composable (appendLoader: @Composable () -> Unit) -> Unit
) {
  Box(modifier = modifier.fillMaxSize()) {
    content {
      if (state == APPENDING) {
        AppendingLoader()
      }
    }

    if (state == LOADING) {
      // TODO: if online use placeholder UI loader.
      CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
        color = Blue300
      )
    }
  }
}

@Composable
fun PullRefreshPanel(
  modifier: Modifier = Modifier,
  state: States,
  onRefresh: () -> Unit = {},
  content: @Composable () -> Unit
) {
  Panel(modifier = modifier, state = state) {
    SwipeRefresh(
      modifier = Modifier.testTag("swipe_refresh"),
      swipeEnabled = state != LOADING,
      state = rememberSwipeRefreshState(isRefreshing = state == REFRESHING),
      onRefresh = onRefresh,
      indicator = { state, refreshTrigger ->
        val colorScheme = MaterialTheme.colorScheme
        SwipeRefreshIndicator(
          state = state,
          refreshTriggerDistance = refreshTrigger,
          backgroundColor = colorScheme.background,
          contentColor = colorScheme.onBackground,
          elevation = if (isSystemInDarkTheme()) 0.dp else 4.dp
        )
      }
    ) {
      content()
    }
  }
}

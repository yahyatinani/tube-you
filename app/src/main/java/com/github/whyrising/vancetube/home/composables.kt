package com.github.whyrising.vancetube.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import com.github.whyrising.vancetube.base.regBaseSubs
import com.github.whyrising.vancetube.initAppDb
import com.github.whyrising.vancetube.ui.anim.enterAnimation
import com.github.whyrising.vancetube.ui.anim.exitAnimation
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.google.accompanist.navigation.animation.composable

data class VideoItem(
  val title: String,
  val thumbnail: Any?,
  val length: String,
  val channelName: String,
  val channelAvatar: String,
  val viewsCount: String,
  val releaseTime: String
)

@Composable
fun Home(modifier: Modifier = Modifier) {
  val homeVideos = listOf(
    VideoItem(
      title = "Cheap and Easy VPN in 5 Minutes!",
      thumbnail = null,
      length = "Jhon Doe",
      channelName = "",
      channelAvatar = "",
      viewsCount = "33K views",
      releaseTime = "2 weeks ago"
    )
  )
  Surface(modifier = modifier.fillMaxSize()) {
    LazyColumn {
      items(homeVideos) { video: VideoItem ->
        Column {
          Image(
            modifier = Modifier
              .fillParentMaxWidth()
              .height(200.dp),
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "thumbnail",
          )
          Row {

          }
        }
      }
    }
  }
}

// -- navigation ---------------------------------------------------------------

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.home(animOffSetX: Int) {
  composable(
    route = home.panel.name,
    exitTransition = { exitAnimation(targetOffsetX = -animOffSetX) },
    popEnterTransition = { enterAnimation(initialOffsetX = -animOffSetX) }
  ) {
    Home()
  }
}

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun HomePreview() {
  initAppDb()
  regBaseSubs()
  regHomeSubs()
  VanceTheme {
    Home()
  }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeDarkPreview() {
  VanceTheme {
    Home()
  }
}

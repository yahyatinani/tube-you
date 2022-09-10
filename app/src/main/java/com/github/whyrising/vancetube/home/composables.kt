package com.github.whyrising.vancetube.home

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.ui.anim.enterAnimation
import com.github.whyrising.vancetube.ui.anim.exitAnimation
import com.github.whyrising.vancetube.ui.theme.Blue300
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun VideoLengthText(
  modifier: Modifier = Modifier,
  videoLength: String
) {
  Text(
    modifier = modifier
      .background(
        color = Color.Black.copy(alpha = .8f),
        shape = RoundedCornerShape(2.dp)
      )
      .padding(horizontal = 3.dp),
    text = videoLength,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.bodySmall.copy(
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
  )
}

@Composable
fun VideoItemTitle(modifier: Modifier = Modifier, viewModel: VideoViewModel) {
  Text(
    text = viewModel.title,
    modifier = modifier,
    maxLines = 2,
    softWrap = true,
    overflow = TextOverflow.Ellipsis,
    style = MaterialTheme.typography.titleMedium.copy(
      fontWeight = FontWeight.W700
    )
  )
}

@Composable
fun VideoItemInfo(
  viewModel: VideoViewModel,
  modifier: Modifier = Modifier,
  textStyle: TextStyle = TextStyle.Default
) {
  val videoInfo = viewModel.info
  val context = LocalContext.current
  val color = LocalContentColor.current.copy(alpha = .4f)
  ClickableText(
    text = videoInfo,
    modifier = modifier,
    style = textStyle.copy(color = color),
    onClick = {
      videoInfo
        .getStringAnnotations("author", it, it)
        .firstOrNull()?.let { stringAnnotation ->
          // TODO: visit the channel of this video by id in stringAnnotation.
          Toast.makeText(
            context,
            "TODO: not implemented yet",
            Toast.LENGTH_SHORT
          ).show()
        }
    }
  )
}

@Composable
fun VideoItemMoreButton(modifier: Modifier = Modifier) {
  IconButton(
    modifier = modifier.size(20.dp),
    onClick = { /*TODO*/ }
  ) {
    Icon(
      imageVector = Icons.Filled.MoreVert,
      contentDescription = "more"
    )
  }
}

@Composable
fun ThumbnailImage(modifier: Modifier = Modifier, url: String?) {
  AsyncImage(
    model = url,
    contentDescription = "thumbnail",
    modifier = modifier
      .background(Color.DarkGray)
      .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val width = placeable.width
        val relativeHeightPx = (width * 720) / 1280
        layout(width, relativeHeightPx) {
          placeable.placeRelative(0, 0)
        }
      },
    contentScale = ContentScale.FillWidth
  )
}

@Composable
fun Thumbnail(
  modifier: Modifier,
  viewModel: VideoViewModel
) {
  Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
    ThumbnailImage(
      modifier = modifier.fillMaxWidth(),
      url = viewModel.thumbnail
    )
    VideoLengthText(
      modifier = Modifier.padding(8.dp),
      videoLength = viewModel.length
    )
  }
}

@Composable
fun VideoItemPortrait(
  modifier: Modifier = Modifier,
  videoInfoTextStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
  viewModel: VideoViewModel
) {
  Column(modifier = Modifier.clickable { /*todo:*/ }) {
    Thumbnail(
      modifier = Modifier.fillMaxWidth(),
      viewModel = viewModel
    )
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(top = 8.dp, end = 4.dp, bottom = 24.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) {
        VideoItemTitle(viewModel = viewModel)
        Spacer(modifier = Modifier.height(4.dp))
        VideoItemInfo(
          viewModel = viewModel,
          textStyle = videoInfoTextStyle
        )
      }

      Spacer(modifier = Modifier.width(24.dp))

      VideoItemMoreButton()
    }
  }
}

@Composable
fun VideoListItemLandscapeCompact(viewModel: VideoViewModel) {
  Row(
    modifier = Modifier
      .testTag("video")
      .padding(vertical = 8.dp)
      .clickable { /*todo:*/ }
  ) {
    Thumbnail(
      modifier = Modifier.weight(.24f),
      viewModel = viewModel
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = Modifier.weight(.8f)) {
      VideoItemTitle(viewModel = viewModel)
      Spacer(modifier = Modifier.height(4.dp))
      VideoItemInfo(viewModel)
    }

    VideoItemMoreButton()
  }
}

@Composable
fun VideosList(
  orientation: Int = 1,
  paddingValues: PaddingValues,
  videos: List<VideoViewModel>
) {
  LazyColumn(
    modifier = Modifier
      .testTag("popular_videos_list")
      .fillMaxSize()
      .then(
        if (orientation == ORIENTATION_PORTRAIT) Modifier
        else Modifier.padding(horizontal = 16.dp)
      ),
    contentPadding = paddingValues
  ) {
    items(
      items = videos,
      key = { it.id }
    ) { viewModel ->
      when (orientation) {
        ORIENTATION_PORTRAIT -> {
          VideoItemPortrait(
            modifier = Modifier.padding(start = 12.dp),
            viewModel = viewModel
          )
        }
        else -> VideoListItemLandscapeCompact(viewModel)
      }
    }
  }
}

@Composable
fun VideosGrid(
  orientation: Int = 1,
  paddingValues: PaddingValues,
  videos: List<VideoViewModel>
) {
  LazyVerticalGrid(
    modifier = Modifier
      .testTag("popular_videos_list")
      .padding(start = 16.dp, end = 16.dp),
    columns = GridCells.Fixed(
      count = if (orientation == ORIENTATION_PORTRAIT) 2 else 3
    ),
    contentPadding = paddingValues,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(50.dp)
  ) {
    items(
      items = videos,
      key = { it.id }
    ) { viewModel ->
      VideoItemPortrait(
        modifier = Modifier
          .testTag("video")
          .padding(bottom = 24.dp),
        videoInfoTextStyle = TextStyle.Default.copy(
          fontSize = 14.sp
        ),
        viewModel = viewModel
      )
    }
  }
}

@Composable
fun Home(
  modifier: Modifier = Modifier,
  paddingValues: PaddingValues = PaddingValues(),
  state: HomePanelState,
  content: @Composable (videos: List<VideoViewModel>) -> Unit
) {
  Box(modifier = modifier.fillMaxSize()) {
    if (state is HomePanelState.Loading) {
      CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
        color = Blue300
      )
    }

    val isMaterialised = state is HomePanelState.Materialised
    val isRefreshing = !isMaterialised && state is HomePanelState.Refreshing
    if (isMaterialised || isRefreshing) {
      SwipeRefresh(
        modifier = Modifier.testTag("swipe_refresh"),
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { dispatch(v(home.refresh, state)) },
        indicator = { state, refreshTrigger ->
          val colorScheme = MaterialTheme.colorScheme
          SwipeRefreshIndicator(
            state = state,
            refreshTriggerDistance = refreshTrigger,
            scale = true,
            backgroundColor = colorScheme.background,
            contentColor = colorScheme.onSurface
          )
        },
        indicatorPadding = paddingValues
      ) {
        val videos = when {
          isMaterialised -> (state as HomePanelState.Materialised).popularVideos
          else -> (state as HomePanelState.Refreshing).currentPopularVideos
        }
        content(videos)
      }
    }
  }
}

// -- navigation ---------------------------------------------------------------
@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.setupHome(
  animOffSetX: Int,
  paddingValues: PaddingValues,
  content: @Composable (videos: List<VideoViewModel>) -> Unit
) {
  composable(
    route = home.panel.name,
    exitTransition = { exitAnimation(targetOffsetX = -animOffSetX) },
    popEnterTransition = { enterAnimation(initialOffsetX = -animOffSetX) }
  ) {
    LaunchedEffect(true) { dispatch(v(home.load)) }

    Home(
      paddingValues = paddingValues,
      state = subscribe<HomePanelState>(v(home.matrialised_state)).w(),
      content = content
    )
  }
}

fun NavGraphBuilder.home(
  animOffSetX: Int,
  paddingValues: PaddingValues,
  orientation: Int
) {
  setupHome(animOffSetX, paddingValues) {
    VideosList(
      orientation = orientation,
      paddingValues = paddingValues,
      videos = it
    )
  }
}

fun NavGraphBuilder.homeLarge(
  animOffSetX: Int,
  paddingValues: PaddingValues,
  orientation: Int
) {
  setupHome(animOffSetX, paddingValues) {
    VideosGrid(
      orientation = orientation,
      paddingValues = paddingValues,
      videos = it
    )
  }
}

// -- Previews -----------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun VideoLengthTextPreview() {
  VanceTheme {
    VideoLengthText(videoLength = "2:23")
  }
}

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
    Home(
      state = HomePanelState.Materialised(designTimeData)
    ) {
      VideosList(
        orientation = 1,
        paddingValues = PaddingValues(),
        videos = it
      )
    }
  }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeDarkPreview() {
  VanceTheme {
    Surface {
      Home(
        state = HomePanelState.Materialised(designTimeData)
      ) {
        VideosList(
          orientation = 1,
          paddingValues = PaddingValues(),
          videos = it
        )
      }
    }
  }
}

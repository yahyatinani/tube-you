package com.github.whyrising.vancetube.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize.Companion.Zero
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.base.AppDb
import com.github.whyrising.vancetube.base.base.is_top_bar_fixed
import com.github.whyrising.vancetube.base.canBeScrolled
import com.github.whyrising.vancetube.base.regBaseSubs
import com.github.whyrising.vancetube.home.home.popular_vids_formatted
import com.github.whyrising.vancetube.initAppDb
import com.github.whyrising.vancetube.ui.anim.enterAnimation
import com.github.whyrising.vancetube.ui.anim.exitAnimation
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalTextApi::class)
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
    style = MaterialTheme.typography.body1.copy(
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
    modifier = modifier.fillMaxWidth(),
    maxLines = 2,
    softWrap = true,
    overflow = TextOverflow.Ellipsis,
    style = MaterialTheme.typography.subtitle2
  )
}

@Composable
fun VideoItemInfo(viewModel: VideoViewModel) {
  val videoInfo = viewModel.info
  val context = LocalContext.current
  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
    val color = LocalContentColor.current.copy(LocalContentAlpha.current)
    ClickableText(
      text = videoInfo,
      style = TextStyle.Default.copy(color = color, fontSize = 12.sp),
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
}

@Composable
fun VideoItemMoreButton() {
  IconButton(
    modifier = Modifier.size(20.dp),
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
      .fillMaxWidth()
      .background(Color.DarkGray)
      .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val width = placeable.width
        val relativeHeightPx = (width * 720) / 1280
        layout(width, relativeHeightPx) {
          placeable.placeRelative(0, 0)
        }
      },
    contentScale = ContentScale.Fit
  )
}

@Composable
fun ThumbnailComposable(
  modifier: Modifier,
  viewModel: VideoViewModel
) {
  Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
    ThumbnailImage(
      modifier = modifier,
      url = viewModel.thumbnail
    )
    VideoLengthText(
      modifier = Modifier.padding(8.dp),
      videoLength = viewModel.length
    )
  }
}

@Composable
fun VideoListItemPortraitCompact(viewModel: VideoViewModel) {
  Column(modifier = Modifier.clickable { /*todo:*/ }) {
    ThumbnailComposable(
      modifier = Modifier.fillMaxWidth(),
      viewModel = viewModel
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp, start = 10.dp, end = 4.dp)
        .padding(bottom = 24.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) {
        VideoItemTitle(viewModel = viewModel)
        Spacer(modifier = Modifier.height(4.dp))
        VideoItemInfo(viewModel)
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
      .padding(vertical = 8.dp)
      .clickable { /*todo:*/ }
  ) {
    ThumbnailComposable(
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

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PopularVideosList(
  paddingValues: PaddingValues = PaddingValues(),
  windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(Zero)
) {
  val state = rememberLazyListState()
  val canBeScrolled by remember { state.canBeScrolled() }
  LaunchedEffect(canBeScrolled) { dispatch(v(is_top_bar_fixed, canBeScrolled)) }
  val videos = subscribe<List<VideoViewModel>>(v(popular_vids_formatted)).w()
  val isHeightCompact =
    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .then(
        if (isHeightCompact) Modifier.padding(horizontal = 16.dp) else Modifier
      ),
    state = state,
    contentPadding = paddingValues
  ) {
    items(
      items = videos,
      key = { it.id }
    ) { viewModel ->
      when {
        isHeightCompact -> VideoListItemLandscapeCompact(viewModel)
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact -> {
          VideoListItemPortraitCompact(viewModel = viewModel)
        }
        else -> {
          TODO("Wide screens")
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun Home(
  paddingValues: PaddingValues = PaddingValues(),
  windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(Zero)
) {
  Surface(modifier = Modifier.fillMaxSize()) {
    val isLoading = subscribe<Boolean>(v(home.is_loading)).w()
    SwipeRefresh(
      state = rememberSwipeRefreshState(
        isRefreshing = subscribe<Boolean>(v(home.is_refreshing)).w()
      ),
      onRefresh = { dispatch(v(home.refresh)) },
      swipeEnabled = !isLoading,
      indicatorPadding = paddingValues
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        PopularVideosList(paddingValues, windowSizeClass)
        if (isLoading) {
          CircularProgressIndicator(color = Color.Cyan)
        }
      }
    }
  }
}

// -- navigation ---------------------------------------------------------------
@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.home(
  animOffSetX: Int,
  paddingValues: PaddingValues,
  windowSizeClass: WindowSizeClass
) {
  composable(
    route = home.panel.name,
    exitTransition = { exitAnimation(targetOffsetX = -animOffSetX) },
    popEnterTransition = { enterAnimation(initialOffsetX = -animOffSetX) }
  ) {
    SideEffect {
      dispatch(v(home.get_popular_vids))
    }
    Home(paddingValues, windowSizeClass)
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

@Preview(showBackground = true)
@Composable
fun VideoListPreview() {
  initAppDb()
  regHomeSubs(LocalContext.current)
  regSub<AppDb, Any>(popular_vids_formatted) { _, _ ->
    v(
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
  }
  regBaseSubs()
  VanceTheme {
    PopularVideosList()
  }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
  regBaseSubs()
  regHomeSubs(LocalContext.current)
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

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
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.base.canBeScrolled
import com.github.whyrising.vancetube.base.regBaseSubs
import com.github.whyrising.vancetube.home.home.popular_vids_formatted
import com.github.whyrising.vancetube.initAppDb
import com.github.whyrising.vancetube.ui.anim.enterAnimation
import com.github.whyrising.vancetube.ui.anim.exitAnimation
import com.github.whyrising.vancetube.ui.theme.Blue300
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.vancetube.ui.theme.composables.isCompact
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
    modifier = modifier,
    maxLines = 2,
    softWrap = true,
    overflow = TextOverflow.Ellipsis,
    style = MaterialTheme.typography.subtitle2.copy(
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
  CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
    val color = LocalContentColor.current.copy(LocalContentAlpha.current)
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
fun PerformantVideoItemPortrait(viewModel: VideoViewModel) {
  ConstraintLayout(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { /*todo:*/ }
  ) {
    val (thumbnailImg, length, title, info, moreBtn) = createRefs()

    val horizontalChain =
      createHorizontalChain(title, moreBtn, chainStyle = ChainStyle.Spread)

    constrain(horizontalChain) {
      start.linkTo(parent.start)
    }

    AsyncImage(
      model = viewModel.thumbnail,
      contentDescription = "thumbnail",
      modifier = Modifier
        .background(Color.DarkGray)
        .layout { measurable, constraints ->
          val placeable = measurable.measure(constraints)
          val width = placeable.width
          val relativeHeightPx = (width * 720) / 1280
          layout(width, relativeHeightPx) {
            placeable.placeRelative(0, 0)
          }
        }
        .constrainAs(thumbnailImg) {
          top.linkTo(parent.top)
          start.linkTo(parent.start)
          end.linkTo(parent.end)
          width = Dimension.fillToConstraints
          height = Dimension.preferredWrapContent
        },
      contentScale = ContentScale.FillWidth
    )
    VideoLengthText(
      modifier = Modifier
        .constrainAs(length) {
          bottom.linkTo(thumbnailImg.bottom, 8.dp)
          end.linkTo(parent.end, 8.dp)
        },
      videoLength = viewModel.length
    )
    Text(
      text = viewModel.title,
      modifier = Modifier
        .constrainAs(title) {
          start.linkTo(parent.start)
          end.linkTo(parent.end)
          top.linkTo(thumbnailImg.bottom, 10.dp)
          width = Dimension.fillToConstraints
        },
      maxLines = 2,
      softWrap = true,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.subtitle2.copy(
        fontWeight = FontWeight.W700
      )
    )

    VideoItemMoreButton(
      modifier = Modifier
        .constrainAs(moreBtn) {
          top.linkTo(thumbnailImg.bottom, 10.dp)
        }
    )

    VideoItemInfo(
      viewModel = viewModel,
      modifier = Modifier
        .padding(bottom = 24.dp)
        .constrainAs(info) {
          start.linkTo(parent.start)
          top.linkTo(title.bottom, margin = 2.dp)
        },
      textStyle = TextStyle.Default.copy(fontSize = 14.sp)
    )
  }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun Home(
  paddingValues: PaddingValues = PaddingValues(),
  windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(Zero),
  orientation: Int = 1
) {
  Box(
    modifier = Modifier
//          .recomposeHighlighter()
      .fillMaxSize(),
  ) {
    if (subscribe<Boolean>(v(home.is_loading)).w()) {
      CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
        color = Blue300
      )
    }

    val isRefreshing = subscribe<Boolean>(v(home.is_refreshing)).w()
    SwipeRefresh(
      state = rememberSwipeRefreshState(isRefreshing),
      onRefresh = { dispatch(v(home.refresh)) },
      modifier = Modifier,
      indicatorPadding = paddingValues
    ) {
      val videos =
        subscribe<List<VideoViewModel>>(v(popular_vids_formatted)).w()
      if (isCompact(windowSizeClass)) {
        val state = rememberLazyListState()
        val canBeScrolled by remember { state.canBeScrolled() }
//            LaunchedEffect(canBeScrolled) {
//            dispatch(v(base.is_top_bar_fixed, canBeScrolled)) }
        LazyColumn(
          modifier = Modifier
//                .recomposeHighlighter()
            .fillMaxSize()
            .then(
              if (orientation == ORIENTATION_PORTRAIT) Modifier
              else Modifier.padding(horizontal = 16.dp)
            ),
          state = state,
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
      } else {
//            Box(modifier = Modifier.fillMaxSize().background(color = Color.Red))
        LazyVerticalGrid(
          modifier = Modifier
            //      .recomposeHighlighter()
            .padding(start = 16.dp, end = 16.dp),
          columns = GridCells.Fixed(
            count = if (orientation == ORIENTATION_PORTRAIT) 2 else 3
          ),
          contentPadding = PaddingValues(top = 72.dp, bottom = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(50.dp)
        ) {
          items(
            items = videos,
            key = { it.id }
          ) { viewModel ->
            VideoItemPortrait(
              modifier = Modifier.padding(bottom = 24.dp),
              videoInfoTextStyle = TextStyle.Default.copy(
                fontSize = 14.sp
              ),
              viewModel = viewModel
            )
            // PerformantVideoItemPortrait(viewModel = viewModel)
          }
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
  windowSizeClass: WindowSizeClass,
  orientation: Int
) {
  composable(
    route = home.panel.name,
    exitTransition = { exitAnimation(targetOffsetX = -animOffSetX) },
    popEnterTransition = { enterAnimation(initialOffsetX = -animOffSetX) }
  ) {
    SideEffect {
      dispatch(v(home.get_popular_vids))
    }
    Home(paddingValues, windowSizeClass, orientation)
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
fun PerformanceItemPreview() {
  VanceTheme {
    PerformantVideoItemPortrait(
      viewModel = VideoViewModel(
        "#ldfj243kj2r",
        "2342lk2sdf",
        "Title title title title title title title title title title " +
          "title title title title title ",
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
}

@Preview(showBackground = true)
@Composable
fun VideoListPreview() {
  initAppDb()
//  VanceTheme {
//    PopularVideosList(
//      videos = v(
//        VideoViewModel(
//          "#ldfj243kj2r",
//          "2342lk2sdf",
//          "Title",
//          "",
//          "2:23",
//          formatVideoInfo(
//            author = "Jon Deo",
//            authorId = "2342lk2sdf",
//            viewCount = "32432",
//            publishedText = "2 hours ago",
//            viewsLabel = "views"
//          )
//        ),
//        VideoViewModel(
//          "#ld2lk43kj2r",
//          "fklj223jflrk23j",
//          "Very long tiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
//            "iiiiiiiiiiiiiiiiiiiiitle",
//          "",
//          "2:23",
//          formatVideoInfo(
//            author = "Jon Deo",
//            authorId = "2342lk2sdf",
//            viewCount = "32432",
//            publishedText = "2 hours ago",
//            viewsLabel = "views"
//          )
//        )
//      )
//    )
//  }
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

package com.github.whyrising.vancetube.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.base.regBaseSubs
import com.github.whyrising.vancetube.initAppDb
import com.github.whyrising.vancetube.ui.anim.enterAnimation
import com.github.whyrising.vancetube.ui.anim.exitAnimation
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

fun constraints(): ConstraintSet = ConstraintSet {
  val videoThumbnail = createRefFor("videoThumbnail")
  val infoRow = createRefFor("infoRow")
  val length = createRefFor("length")
  val bottomBarrier = createBottomBarrier(videoThumbnail, margin = 8.dp)

  constrain(videoThumbnail) {
    top.linkTo(parent.top)
  }

  constrain(infoRow) {
    top.linkTo(bottomBarrier)
    absoluteRight.linkTo(parent.absoluteRight)
    absoluteLeft.linkTo(parent.absoluteLeft)
  }

  constrain(length) {
    bottom.linkTo(videoThumbnail.bottom, margin = 8.dp)
    absoluteRight.linkTo(parent.absoluteRight, margin = 8.dp)
  }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun VideoLengthText(
  modifier: Modifier = Modifier,
  videoLength: String
) {
  Text(
    modifier = modifier
      .layoutId("length")
      .background(
        color = Color.Black.copy(alpha = .8f),
        shape = RoundedCornerShape(2.dp)
      )
      .padding(horizontal = 3.dp),
    text = videoLength,
    textAlign = TextAlign.End,
    style = MaterialTheme.typography.overline.copy(
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      platformStyle = PlatformTextStyle(includeFontPadding = false),
    )
  )
}

@Composable
fun VideoItem(
  constraints: ConstraintSet,
  vidThumbnail: String?,
  vidTitle: String,
  vidLength: String,
  vidInfo: AnnotatedString,
  height: Dp
) {
  ConstraintLayout(
    constraintSet = constraints,
    modifier = Modifier.clickable(onClick = {})
  ) {
    AsyncImage(
      model = vidThumbnail,
      contentDescription = "thumbnail",
      modifier = Modifier
        .layoutId("videoThumbnail")
        .height(height)
        .background(Color.DarkGray),
      contentScale = ContentScale.FillWidth
    )

    VideoLengthText(videoLength = vidLength)

    Row(
      modifier = Modifier
        .layoutId("infoRow")
        .fillMaxWidth()
        .padding(start = 10.dp, end = 4.dp)
        .padding(bottom = 20.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = vidTitle,
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2,
          softWrap = true,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.h6.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
          )
        )
        Spacer(modifier = Modifier.height(4.dp))
        CompositionLocalProvider(LocalContentAlpha provides medium) {
          Text(
            text = vidInfo,
            style = MaterialTheme.typography.body2.copy(fontSize = 12.sp),
          )
        }
      }

      Spacer(modifier = Modifier.width(24.dp))

      IconButton(
        modifier = Modifier.size(20.dp),
        onClick = { /*TODO*/ },
      ) {
        Icon(
          imageVector = Icons.Filled.MoreVert,
          contentDescription = "more"
        )
      }
    }
  }
}

@Composable
fun Home(popularVideos: List<IPersistentMap<VideoIds, Any>> = v()) {
  Surface(modifier = Modifier.fillMaxSize()) {
    SideEffect {
      dispatch(v(home.get_popular_vids))
      dispatch(v(home.video_item_height))
    }
    val isRefreshing by remember { mutableStateOf(false) }
    val height = subscribe<Int>(v(home.video_item_height)).w().dp
    SwipeRefresh(
      state = rememberSwipeRefreshState(isRefreshing),
      onRefresh = { /*TODO: refresh home*/ },
    ) {
      LazyColumn {
        items(popularVideos) { video: IPersistentMap<VideoIds, Any> ->
          VideoItem(
            constraints = constraints(),
            height = height,
            vidThumbnail = video[VideoIds.thumbnail] as String?,
            vidTitle = video[VideoIds.title] as String,
            vidLength = video[VideoIds.length] as String,
            vidInfo = video[VideoIds.info] as AnnotatedString
          )
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
    Home(
      popularVideos = subscribe<List<IPersistentMap<VideoIds, Any>>>(
        v(home.popular_vids_formatted)
      ).w()
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

@Preview(showBackground = true)
@Composable
fun HomePreview() {
  initAppDb()
  regBaseSubs()
  regHomeSubs()
  VanceTheme {
    Home(
      popularVideos = v(
        m(
          VideoIds.title to "Title",
          VideoIds.thumbnail to "",
          VideoIds.length to "2:23",
          VideoIds.info to formatVideoInfo(
            viewCount = "32432",
            author = "Jon Deo",
            publishedText = "2 hours ago"
          )
        )
      ),
    )
  }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeDarkPreview() {
  VanceTheme {
    Home(
      popularVideos = v(
        m(
          VideoIds.title to "Title",
          VideoIds.thumbnail to "",
          VideoIds.length to "2:23",
          VideoIds.info to formatVideoInfo(
            viewCount = "32432",
            author = "Jon Deo",
            publishedText = "2 hours ago"
          )
        )
      )
    )
  }
}

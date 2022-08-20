package com.github.whyrising.vancetube.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.base.AppDb
import com.github.whyrising.vancetube.base.regBaseSubs
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
      platformStyle = PlatformTextStyle(includeFontPadding = false),
    )
  )
}

@Composable
fun LoadingIndicator() {
  if (subscribe<Boolean>(v(home.is_loading)).w())
    CircularProgressIndicator(color = Color.Cyan)
}

@Composable
fun Thumbnail(modifier: Modifier = Modifier, url: String?) {
  AsyncImage(
    model = url,
    contentDescription = "thumbnail",
    modifier = modifier
      .background(Color.DarkGray),
    contentScale = ContentScale.Fit
  )
}

@Composable
fun VideoListItem(viewModel: VideoViewModel) {
  Column(modifier = Modifier.clickable { /*todo:*/ }) {
    Box(contentAlignment = Alignment.BottomEnd) {
      Thumbnail(
        modifier = Modifier
          .height(subscribe<Int>(v(home.thumbnail_height)).w().dp),
        url = viewModel.thumbnail,
      )
      VideoLengthText(
        modifier = Modifier.padding(8.dp),
        videoLength = viewModel.length
      )
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp, start = 10.dp, end = 4.dp)
        .padding(bottom = 20.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = viewModel.title,
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
            text = viewModel.info,
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
fun PopularVideosList() {
  val videos =
    subscribe<List<VideoViewModel>>(v(home.popular_vids_formatted)).w()
  LazyColumn(modifier = Modifier.fillMaxSize()) {
    items(
      items = videos,
      key = { it.id },
    ) { videoVm ->
      VideoListItem(viewModel = videoVm)
    }
  }
}

@Composable
fun Home() {
  val isRefreshing by remember { mutableStateOf(false) }
  Surface(modifier = Modifier.fillMaxSize()) {
    SwipeRefresh(
      state = rememberSwipeRefreshState(isRefreshing),
      onRefresh = { /*TODO: refresh home*/ },
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        PopularVideosList()
        LoadingIndicator()
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
    SideEffect {
      dispatch(v(home.get_popular_vids))
      dispatch(v(home.thumbnail_height))
    }
    Home()
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
  regHomeSubs()
  regSub<AppDb, Any>(home.popular_vids_formatted) { db, _ ->
    v(
      VideoViewModel(
        "#ldfj243kj2r",
        "Title",
        "",
        "2:23",
        formatVideoInfo(
          viewCount = "32432",
          author = "Jon Deo",
          publishedText = "2 hours ago"
        )
      ),
      VideoViewModel(
        "#ld2lk43kj2r",
        "Very long tiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
          "iiiiiiiiiiiiiiiiiiiiitle",
        "",
        "2:23",
        formatVideoInfo(
          viewCount = "32432",
          author = "Jon Deo",
          publishedText = "2 hours ago"
        )
      ),
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

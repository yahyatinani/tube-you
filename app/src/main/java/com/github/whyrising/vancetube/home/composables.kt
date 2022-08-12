package com.github.whyrising.vancetube.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.navigation.NavGraphBuilder
import coil.compose.AsyncImage
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.R
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.vancetube.base.regBaseSubs
import com.github.whyrising.vancetube.initAppDb
import com.github.whyrising.vancetube.ui.anim.enterAnimation
import com.github.whyrising.vancetube.ui.anim.exitAnimation
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.y.core.collections.PersistentVector
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

@Composable
fun VideoItem(
  constraints: ConstraintSet,
  vidThumbnail: String?,
  vidTitle: String,
  vidLength: String,
  vidChannelAvatar: String,
  vidInfo: AnnotatedString
) {
  ConstraintLayout(
    constraintSet = constraints,
    modifier = Modifier.clickable(onClick = {})
  ) {
    AsyncImage(
      model = vidThumbnail,
      placeholder = painterResource(R.drawable.ic_launcher_background),
      contentDescription = "thumbnail",
      modifier = Modifier
        .layoutId("videoThumbnail")
        .background(Color.Black)
        .fillMaxWidth()
        .height(232.dp),
    )

    Text(
      text = vidLength,
      style = MaterialTheme.typography.overline.copy(
        fontWeight = FontWeight.Bold,
        color = Color.White
      ),
      modifier = Modifier
        .layoutId("length")
        .clip(RoundedCornerShape(2.dp))
        .background(color = Color.Black)
        .padding(1.dp)
    )

    Row(
      modifier = Modifier
        .layoutId("infoRow")
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .padding(bottom = 20.dp)
    ) {
//      AsyncImage(
//        modifier = Modifier
//          .padding(top = 4.dp)
//          .size(48.dp)
//          .clip(CircleShape)
//          .clickable { },
//        model = vidChannelAvatar,
//        contentDescription = "Channel avatar",
//        placeholder = painterResource(id = R.drawable.ic_launcher_background),
//      )

//      Spacer(modifier = Modifier.width(16.dp))
 
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = vidTitle,
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2,
          softWrap = true,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.h6.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        )

        Text(
          text = vidInfo,
          style = MaterialTheme.typography.caption,
        )
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
fun Home(modifier: Modifier = Modifier) {
  Surface {
    val isRefreshing by remember { mutableStateOf(false) }
    SwipeRefresh(
      state = rememberSwipeRefreshState(isRefreshing),
      onRefresh = { /*TODO: refresh home*/ },
    ) {
      SideEffect {
        dispatch(v(home.get_popular_vids))
      }
      val items =
        subscribe<PersistentVector<VideoMetadata>>(v(home.popularVids)).w()
      LazyColumn(
        modifier = modifier
      ) {
        items(items) { video: VideoMetadata ->
          val divider = " • "
          val vidInfo = buildAnnotatedString {
            append(video.author)
            append(divider)
            append(video.viewCount)
            append(" views")
            append(divider)
            append(video.publishedText)
          }
          VideoItem(
            constraints = constraints(),
            vidTitle = video.title,
            vidThumbnail = video.videoThumbnails[1].url,
            vidLength = "${video.lengthSeconds}",
            vidChannelAvatar = "",
            vidInfo = vidInfo
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

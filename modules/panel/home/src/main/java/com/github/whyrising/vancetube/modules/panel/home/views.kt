package com.github.whyrising.vancetube.modules.panel.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.watch
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.designsystem.component.VideosGrid
import com.github.whyrising.vancetube.modules.designsystem.component.VideosList
import com.github.whyrising.vancetube.modules.designsystem.component.VideosPanel
import com.github.whyrising.vancetube.modules.designsystem.data.Videos
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.CoroutineScope

// -- navigation ---------------------------------------------------------------

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.homeCommon(
  content: @Composable (videos: Videos) -> Unit
) {
  composable(
    route = home.route.toString()
  ) {
    val scope: CoroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
      regCofx(home.coroutine_scope) { cofx ->
        cofx.assoc(home.coroutine_scope, scope)
      }
      regHomeEvents
      dispatch(v(home.initialize))
    }
    regHomeSubs

    VideosPanel(
      state = watch(v(home.view_model, stringResource(R.string.views_label))),
      onRefresh = { dispatch(v(home.refresh)) },
      content = content
    )
  }
}

fun NavGraphBuilder.home(orientation: Int) {
  homeCommon { videos ->
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
      dispatch(v(common.expand_top_app_bar))
      regScrollToTopListFx(scope) {
        listState.animateScrollToItem(0)
      }
    }

    VideosList(
      orientation = orientation,
      listState = listState,
      videos = videos
    )
  }
}

fun NavGraphBuilder.homeLarge(orientation: Int) {
  homeCommon { videos ->
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()
    LaunchedEffect(Unit) {
      regScrollToTopListFx(scope) {
        gridState.animateScrollToItem(0)
      }
    }
    VideosGrid(
      orientation = orientation,
      gridState = gridState,
      videos = videos
    )
  }
}

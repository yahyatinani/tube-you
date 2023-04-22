package com.github.whyrising.vancetube.modules.panel.home

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.watch
import com.github.whyrising.vancetube.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.HOME_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.designsystem.component.VideosGrid
import com.github.whyrising.vancetube.modules.designsystem.component.VideosList
import com.github.whyrising.vancetube.modules.designsystem.component.VideosPanel
import com.github.whyrising.vancetube.modules.designsystem.data.Videos
import com.github.whyrising.vancetube.modules.panel.common.R
import com.github.whyrising.vancetube.modules.panel.common.searchResults
import com.github.whyrising.y.core.v

// -- navigation ---------------------------------------------------------------

private fun NavGraphBuilder.homeCommon(
  content: @Composable (videos: Videos) -> Unit
) {
  composable(route = HOME_ROUTE) {
    getRegHomeSubs()
    VideosPanel(
      panelVm = watch(v(home.view_model, stringResource(R.string.views_label))),
      onRefresh = { dispatch(v(home.refresh)) },
      content = content
    )
  }
}

fun NavGraphBuilder.home(orientation: Int, thumbnailHeight: Dp) {
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
      videos = videos,
      thumbnailHeight = thumbnailHeight
    )
  }
}

fun NavGraphBuilder.homeLarge(orientation: Int, thumbnailHeight: Dp) {
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
      videos = videos,
      thumbnailHeight = thumbnailHeight
    )
  }
}

fun NavGraphBuilder.homeGraph(
  isCompactDisplay: Boolean,
  orientation: Int,
  thumbnailHeight: Dp
) {
  navigation(route = HOME_GRAPH_ROUTE, startDestination = HOME_ROUTE) {
    if (isCompactDisplay) home(
      orientation = orientation,
      thumbnailHeight = thumbnailHeight
    )
    else homeLarge(orientation = orientation, thumbnailHeight = thumbnailHeight)

    searchResults(
      route = HOME_GRAPH_ROUTE,
      orientation = orientation,
      thumbnailHeight = thumbnailHeight
    )
  }
}

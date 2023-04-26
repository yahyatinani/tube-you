package com.github.yahyatinani.tubeyou.modules.panel.home

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.watch
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.designsystem.component.VideosGrid
import com.github.yahyatinani.tubeyou.modules.designsystem.component.VideosList
import com.github.yahyatinani.tubeyou.modules.designsystem.component.VideosPanel
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.panel.common.search.searchPanel
import kotlinx.coroutines.CoroutineScope

@Composable
private fun InitHome() {
  getRegHomeSubs()
  val scope: CoroutineScope = rememberCoroutineScope()
  LaunchedEffect(Unit) {
    getRegHomeCofx(scope)
    regHomeEvents()
    dispatch(v(home.initialize))
  }
}

private fun NavGraphBuilder.homeCommon(
  content: @Composable (videos: Videos) -> Unit
) {
  composable(route = HOME_ROUTE) {
    InitHome()
    VideosPanel(
      panelVm = watch(v(home.view_model, LocalContext.current.resources)),
      onRefresh = { dispatch(v(home.refresh)) },
      content = content
    )
  }
}

fun NavGraphBuilder.home(orientation: Int, thumbnailHeight: Dp) =
  homeCommon { videos ->
    val listState = rememberLazyListState()
    RegScrollToTopListFx {
      listState.animateScrollToItem(index = 0)
    }

    VideosList(
      orientation = orientation,
      listState = listState,
      videos = videos,
      thumbnailHeight = thumbnailHeight
    )
  }

fun NavGraphBuilder.homeLarge(orientation: Int, thumbnailHeight: Dp) =
  homeCommon { videos ->
    val gridState = rememberLazyGridState()
    RegScrollToTopListFx {
      gridState.animateScrollToItem(index = 0)
    }

    VideosGrid(
      orientation = orientation,
      gridState = gridState,
      videos = videos,
      thumbnailHeight = thumbnailHeight
    )
  }

fun NavGraphBuilder.homeGraph(
  isCompactSize: Boolean,
  orientation: Int,
  thumbnailHeight: Dp
) = navigation(route = HOME_GRAPH_ROUTE, startDestination = HOME_ROUTE) {
  if (isCompactSize) {
    home(orientation = orientation, thumbnailHeight = thumbnailHeight)
  } else {
    homeLarge(orientation = orientation, thumbnailHeight = thumbnailHeight)
  }

  searchPanel(
    route = HOME_GRAPH_ROUTE,
    orientation = orientation,
    thumbnailHeight = thumbnailHeight
  )
}

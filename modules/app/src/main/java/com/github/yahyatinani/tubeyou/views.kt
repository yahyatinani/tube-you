package com.github.yahyatinani.tubeyou

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_navigation_item
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.close_stream
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.expand_player_sheet
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.expand_top_app_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.start_destination
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.activate_searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.clear_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.show_search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.update_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyBottomNavigationBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TySearchBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.thumbnailHeight
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.isCompact
import com.github.yahyatinani.tubeyou.modules.panel.common.Stream
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBar
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.VideoView
import com.github.yahyatinani.tubeyou.modules.panel.home.homeGraph
import com.github.yahyatinani.tubeyou.modules.panel.library.libraryGraph
import com.github.yahyatinani.tubeyou.modules.panel.subscriptions.subsGraph
import io.github.yahyatinani.recompose.cofx.regCofx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.recompose.regFx
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.updateIn
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch

// -- Navigation ---------------------------------------------------------------

private fun navGraphRoute(destination: NavDestination) =
  destination.hierarchy.toList().dropLast(1).last().route!!

private val navChangedListener: (
  controller: NavController,
  destination: NavDestination,
  arguments: Bundle?
) -> Unit = { navCtrl, destination, _ ->
  navCtrl.apply {
    destination.route?.let {
      dispatch(v(active_navigation_item, navGraphRoute(destination)))
    }
  }
}

@Composable
private fun NavigationChangedListenerEffect(navController: NavHostController) {
  DisposableEffect(navController) {
    navController.addOnDestinationChangedListener(navChangedListener)

    onDispose {
      navController.removeOnDestinationChangedListener(navChangedListener)
    }
  }
}

// -- Views --------------------------------------------------------------------

/**
 * @param isPlayerVisible this is needed to avoid late loading a stream after
 * the the player sheet was closed before the loading completed.
 */
@Composable
fun PlaybackBottomSheet(
  streamData: IPersistentMap<Any, Any>?,
  isCollapsed: Boolean,
  isPlaying: Boolean,
  thumbnail: String?,
  showPlayerThumbnail: Boolean,
  isPlayerVisible: Boolean,
  closeVideo: () -> Unit = {},
  togglePlayPause: () -> Unit = {},
  onTapMiniPlayer: () -> Unit = {}
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onTapMiniPlayer),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      val playerModifier = if (isCollapsed) {
        Modifier
          .height(110.dp - 48.dp)
          .aspectRatio(16 / 9f)
      } else if (streamData != null) {
        Modifier
          .fillMaxWidth()
          .aspectRatio(get(streamData, Stream.aspect_ratio)!!)
      } else {
        Modifier
          .fillMaxWidth()
          .aspectRatio(16 / 9f)
      }
      if (isPlayerVisible) {
        VideoView(
          modifier = playerModifier,
          streamData = streamData,
          thumbnail = thumbnail,
          showPlayerThumbnail = showPlayerThumbnail,
          isCollapsed = isCollapsed
        )
      }
      if (isCollapsed) {
        Row(
          modifier = Modifier.height(110.dp - 48.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          val color = MaterialTheme.colorScheme.onBackground
          val colorFilter = ColorFilter.tint(color = color)
          IconButton(onClick = togglePlayPause) {
            val playerIcon = when (isPlaying) {
              true -> Default.Pause
              else -> Default.PlayArrow
            }
            Image(
              imageVector = playerIcon,
              contentDescription = "play/pause",
              colorFilter = colorFilter
            )
          }
          IconButton(onClick = closeVideo) {
            Image(
              imageVector = Default.Close,
              contentDescription = "close video",
              colorFilter = colorFilter
            )
          }
        }
      }
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun topAppBarScrollBehavior(
  isCompactDisplay: Boolean,
  topAppBarState: TopAppBarState,
  searchBar: Any?
): TopAppBarScrollBehavior = when {
  isCompactDisplay && searchBar == null -> {
    LaunchedEffect(Unit) {
      regFx(expand_top_app_bar) {
        topAppBarState.heightOffset = 0f
      }
      regEventFx(expand_top_app_bar) { _, _ ->
        m(fx to v(v(expand_top_app_bar)))
      }
    }
    enterAlwaysScrollBehavior(topAppBarState)
  }

  else -> pinnedScrollBehavior()
}

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalComposeUiApi::class,
  ExperimentalLayoutApi::class
)
@Composable
fun TyApp(
  windowSizeClass: WindowSizeClass,
  navController: NavHostController = rememberNavController()
) {
  NavigationChangedListenerEffect(navController)

  val appScope = rememberCoroutineScope()
  LaunchedEffect(Unit) {
    regAppFx(navController, appScope)
    regCofx(start_destination) { cofx ->
      cofx.assoc(
        start_destination,
        navController.graph.findStartDestination().id
      )
    }
  }

  val isCompactSize = isCompact(windowSizeClass)

  TyTheme(isCompact = isCompactSize) {
    val colorScheme = MaterialTheme.colorScheme
    val sb = watch<SearchBar?>(v(search.search_bar))
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = topAppBarScrollBehavior(isCompactSize, topBarState, sb)

    regCofx(common.coroutine_scope) { cofx ->
      cofx.assoc(common.coroutine_scope, appScope)
    }
    val orientation = LocalConfiguration.current.orientation

    val streamData =
      watch<IPersistentMap<Any, Any>?>(query = v("currently_playing"))

    val sheetState =
      rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val sheetScaffoldState = rememberBottomSheetScaffoldState(sheetState)
    val isCollapsed =
      sheetScaffoldState.bottomSheetState.currentValue.ordinal == 2
    val isPlaying = watch<Boolean>(query = v("is_playing"))
    val thumbnail = watch<String?>(query = v("current_video_thumbnail"))
    val showPlayerThumbnail = watch<Boolean>(query = v("show_player_thumbnail"))

    if (orientation == ORIENTATION_LANDSCAPE && streamData != null) {
      LaunchedEffect(Unit) {
        dispatch(v(":player_fullscreen_landscape"))
      }

      VideoView(
        modifier = Modifier.fillMaxSize(),
        streamData = streamData,
        thumbnail = thumbnail,
        showPlayerThumbnail = showPlayerThumbnail,
        isCollapsed = isCollapsed
      )
      return@TyTheme
    }

    Scaffold(
      bottomBar = {
        TyBottomNavigationBar(
          navItems = watch(v(common.navigation_items)),
          isCompact = isCompactSize,
          colorScheme = colorScheme
        ) { dispatch(v(common.on_click_nav_item, it)) }
      }
    ) { p1 ->
      LaunchedEffect(Unit) {
        snapshotFlow { sheetState.isVisible }.collect { isVisible ->
          if (!isVisible) {
            dispatchSync(v(close_stream))
          }
        }
      }

      BottomSheetScaffold(
        modifier = Modifier
          .padding(p1)
          .fillMaxSize()
          .nestedScroll(scrollBehavior.nestedScrollConnection)
          .semantics {
            // Allows to use testTag() for UiAutomator resource-id.
            testTagsAsResourceId = true
          },
        scaffoldState = sheetScaffoldState,
        sheetPeekHeight = 110.dp,
        sheetDragHandle = null,
        sheetShape = RoundedCornerShape(0.dp),
        sheetContent = {
          val sheetScope = rememberCoroutineScope()
          LaunchedEffect(Unit) {
            dispatch(v(":player_portrait"))
            regFx(common.play_new_stream) {
              sheetScope.launch { sheetState.expand() }
              dispatchSync(v(common.close_player))
            }

            regFx(expand_player_sheet) {
              sheetScope.launch { sheetState.expand() }
            }

            regFx("hide_player_sheet") {
              sheetScope.launch { sheetState.hide() }
            }

            regEventFx(expand_player_sheet) { _, _ ->
              m(fx to v(v(expand_player_sheet)))
            }

            regEventFx(close_stream) { cofx, _ ->
              val appDb = appDbBy(cofx = cofx)
                .dissoc("current_video_stream")
                .assoc("is_player_sheet_visible", false)

              val newAppDb = updateIn(
                appDb,
                l(common.active_stream),
                { map: IPersistentMap<Any?, *> -> map.dissoc("videoId") }
              )
              m(
                recompose.db to newAppDb,
                fx to v(v("hide_player_sheet"), v(common.close_player))
              )
            }
          }
          val isPlayerVisible = watch<Boolean>(v("is_player_sheet_visible"))
          PlaybackBottomSheet(
            streamData = streamData,
            isCollapsed = isCollapsed,
            isPlaying = isPlaying,
            thumbnail = thumbnail,
            isPlayerVisible = isPlayerVisible,
            showPlayerThumbnail = showPlayerThumbnail,
            closeVideo = { dispatchSync(v(close_stream)) },
            togglePlayPause = { dispatchSync(v(common.toggle_player)) },
            onTapMiniPlayer = {
              dispatch(v(expand_player_sheet))
            }
          )
        }
      ) {
        Scaffold(
          topBar = {
            when {
              sb != null -> {
                val topBarScope = rememberCoroutineScope()
                regCofx(search.coroutine_scope) { cofx ->
                  cofx.assoc(search.coroutine_scope, topBarScope)
                }
                TySearchBar(
                  searchQuery = sb[searchBar.query]!! as String,
                  onQueryChange = {
                    dispatchSync(v(search.panel_fsm, update_search_input, it))
                  },
                  onSearch = {
                    dispatch(v(search.panel_fsm, search.submit, it))
                  },
                  isActive = sb[fsm._state] as Boolean,
                  onActiveChange = {
                    dispatch(v(search.panel_fsm, activate_searchBar))
                  },
                  clearInput = {
                    dispatchSync(v(search.panel_fsm, clear_search_input))
                  },
                  backPress = {
                    dispatch(v(search.panel_fsm, search.back_press_search))
                  },
                  suggestions = sb[searchBar.suggestions] as List<String>,
                  colorScheme = colorScheme
                ) { selectedSuggestion ->
                  // FIXME: Move cursor to the end of text.
                  dispatchSync(
                    v(search.panel_fsm, update_search_input, selectedSuggestion)
                  )
                }

                BackHandler {
                  dispatch(v(search.panel_fsm, search.back_press_search))
                }
              }

              else -> {
                TopAppBar(
                  modifier = Modifier.fillMaxWidth(),
//                .windowInsetsPadding(
//                  insets = WindowInsets.safeDrawing.only(Top + Horizontal)
//                )
//                .padding(end = if (isCompactSize) 4.dp else 16.dp),
                  title = {},
                  scrollBehavior = scrollBehavior,
                  navigationIcon = {},
                  actions = {
                    IconButton(
                      onClick = {
                        dispatchSync(v(search.panel_fsm, show_search_bar))
                      }
                    ) {
                      Icon(
                        imageVector = Icons.Outlined.Search,
                        modifier = Modifier.size(26.dp),
                        contentDescription = "Search a video"
                      )
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                      Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "profile picture",
                        tint = colorScheme.onSurface
                      )
                    }
                  },
                  colors = topAppBarColors(
                    scrolledContainerColor = colorScheme.background
                  )
                )
              }
            }
          }
        ) { p2 ->
          val enabled = !watch<Boolean>(query = v(common.is_backstack_empty))
          BackHandler(enabled) { dispatchSync(v(common.bottom_bar_back_press)) }

          val thumbnailHeight = thumbnailHeight(orientation)
          NavHost(
            navController = navController,
            startDestination = HOME_GRAPH_ROUTE,
            modifier = Modifier
              .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
              .padding(p2)
              .consumeWindowInsets(p2)
          ) {
            homeGraph(isCompactSize, orientation, thumbnailHeight)
            subsGraph(isCompactSize, orientation, thumbnailHeight)
            libraryGraph(isCompactSize, orientation, thumbnailHeight)
          }
        }
      }
    }
  }
}

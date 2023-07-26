package com.github.yahyatinani.tubeyou

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.expand_top_app_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.start_destination
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.activate_searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.clear_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.show_search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.update_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.BOTTOM_BAR_HEIGHT
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyBottomNavigationBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TySearchBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.thumbnailHeight
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.isCompact
import com.github.yahyatinani.tubeyou.modules.panel.common.UIState
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBar
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.MINI_PLAYER_HEIGHT
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.PlaybackBottomSheet
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.RegPlayerSheetEffects
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.VideoPlayer
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm.PlayerSheetState
import com.github.yahyatinani.tubeyou.modules.panel.home.homeGraph
import com.github.yahyatinani.tubeyou.modules.panel.library.libraryGraph
import com.github.yahyatinani.tubeyou.modules.panel.subscriptions.subsGraph
import com.github.yahyatinani.tubeyou.nav.NavigationChangedListenerEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.yahyatinani.recompose.cofx.regCofx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.recompose.regFx
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun topAppBarScrollBehavior(
  isCompactDisplay: Boolean,
  topAppBarState: TopAppBarState,
  searchBar: SearchBar?
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
  val density = LocalDensity.current
  val configuration = LocalConfiguration.current

  val pairWh = with(density) {
    configuration.screenWidthDp.dp.toPx() to
      configuration.screenHeightDp.dp.toPx()
  }
  LaunchedEffect(Unit) {
    regAppFx(navController, appScope)

    regCofx(start_destination) { cofx ->
      cofx.assoc(
        start_destination,
        navController.graph.findStartDestination().id
      )
    }
    regCofx(":screen_dimen_px") { coeffects ->
      coeffects.assoc(":screen_dimen_px", pairWh)
    }
  }

  val isCompactSize = isCompact(windowSizeClass)

  TyTheme(isCompact = isCompactSize) {
    val sb = watch<SearchBar?>(v(search.search_bar))
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = topAppBarScrollBehavior(isCompactSize, topBarState, sb)

    regCofx(common.coroutine_scope) { cofx ->
      cofx.assoc(common.coroutine_scope, appScope)
    }

    val playbackFsm =
      watch<IPersistentMap<Any, Any>>(query = v("stream_panel_fsm"))
    val playbackMachine = get<Any>(playbackFsm, fsm._state)

    val playerSheetRegion =
      get<PlayerSheetState>(playbackMachine, ":player_sheet")

    val colorScheme = MaterialTheme.colorScheme

    val systemUiController = rememberSystemUiController()
    LaunchedEffect(isSystemInDarkTheme(), playerSheetRegion) {
      if (playerSheetRegion == PlayerSheetState.EXPANDED) {
        systemUiController.setStatusBarColor(Color.Black)
      } else {
        systemUiController.setSystemBarsColor(color = colorScheme.surface)
      }
    }

    val isCollapsed = playerSheetRegion == PlayerSheetState.COLLAPSED

    val showThumbnail = get<Boolean>(playbackFsm, "show_player_thumbnail")

    val context = LocalContext.current
    val activeStream = watch<UIState>(v("active_stream", context))

    val activeStreamVm = watch<VideoViewModel>(v("active_stream_vm"))
    val orientation = LocalConfiguration.current.orientation

    // WARNING: this must be called before landscape video player
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
      bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
      )
    )

    if (
      orientation == ORIENTATION_LANDSCAPE &&
      playerSheetRegion != PlayerSheetState.HIDDEN
    ) {
      LaunchedEffect(Unit) {
        dispatch(v(":player_fullscreen_landscape"))
      }
      VideoPlayer(
        modifier = Modifier.padding(start = 24.dp),
        stream = activeStream,
        useController = !isCollapsed,
        showThumbnail = showThumbnail,
        thumbnail = activeStreamVm.thumbnail
      )

      return@TyTheme
    }

    val playerSheetState = bottomSheetScaffoldState.bottomSheetState
    val playbackTargetValue = playerSheetState.targetValue

    Scaffold(
      bottomBar = {
        if (playbackTargetValue == SheetValue.Expanded) {
          return@Scaffold
        }

        TyBottomNavigationBar(
          navItems = watch(v(common.navigation_items)),
          isCompact = isCompactSize,
          colorScheme = colorScheme
        ) { dispatch(v(common.on_click_nav_item, it)) }
      }
    ) { p1 ->
      val playerSheetValue = playerSheetState.currentValue
      LaunchedEffect(playerSheetValue) {
        dispatch(v("stream_panel_fsm", playerSheetValue))
      }

      RegPlayerSheetEffects(playerSheetState)

      val sheetPeekHeight = remember(playbackTargetValue, playerSheetValue) {
        when {
          orientation == ORIENTATION_LANDSCAPE -> 0.dp
          playerSheetRegion == PlayerSheetState.HIDDEN -> 0.dp
          playbackTargetValue == SheetValue.Hidden &&
            playerSheetValue == SheetValue.Hidden -> 0.dp

          else -> MINI_PLAYER_HEIGHT + BOTTOM_BAR_HEIGHT
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
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = sheetPeekHeight,
        sheetDragHandle = null,
//        sheetSwipeEnabled = false,
        sheetShape = RoundedCornerShape(0.dp),
        sheetContent = {
          if (orientation == ORIENTATION_LANDSCAPE) return@BottomSheetScaffold

          val playerScope = rememberCoroutineScope()
          LaunchedEffect(Unit) {
            regCofx("player_scope") { cofx ->
              cofx.assoc("player_scope", playerScope)
            }
          }

          val peekHeight = with(LocalDensity.current) {
            get<Float>(playbackFsm, ":desc_sheet_height")?.toDp() ?: 0.toDp()
          }

          PlaybackBottomSheet(
            isCollapsed = isCollapsed,
            onCollapsedClick = {
              dispatch(v<Any>("stream_panel_fsm", common.expand_player_sheet))
            },
            activeStream = activeStream,
            activeStreamCache = activeStreamVm,
            showThumbnail = showThumbnail,
            sheetPeekHeight = peekHeight
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
                    v(
                      search.panel_fsm,
                      update_search_input,
                      selectedSuggestion
                    )
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

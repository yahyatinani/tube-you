package io.github.yahyatinani.tubeyou.ui

import android.app.Activity
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Vertical
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.BOTTOM_BAR_HEIGHT
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBarItem
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TySearchBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyTopAppBar
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.isCompact
import io.github.yahyatinani.recompose.RegCofx
import io.github.yahyatinani.recompose.RegFx
import io.github.yahyatinani.recompose.clearEvent
import io.github.yahyatinani.recompose.clearFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.httpfx.bounce
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.httpfx.regBounceFx
import io.github.yahyatinani.recompose.httpfx.regHttpKtor
import io.github.yahyatinani.recompose.pagingfx.paging
import io.github.yahyatinani.recompose.pagingfx.regPagingFx
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.navigation.NavStateSaver
import io.github.yahyatinani.tubeyou.navigation.RegNavCofx
import io.github.yahyatinani.tubeyou.navigation.RegNavFx
import io.github.yahyatinani.tubeyou.navigation.TopLevelNavItems
import io.github.yahyatinani.tubeyou.navigation.TyNavHost
import io.github.yahyatinani.tubeyou.navigation.topLevelNavItems
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.db.SearchBar
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.evnts.RegSearchEvents
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.subs.RegSearchSubs
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.events.RegWatchEvents
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.PlayerSheetState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fx.RegPlayerSheetEffects
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen.MINI_PLAYER_HEIGHT
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen.NowPlayingSheet
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen.VideoPlayer
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs.RegWatchSubs
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun topAppBarScrollBehavior(
  topAppBarState: TopAppBarState,
  isSearchScreen: Boolean,
  isCompactDisplay: Boolean
): TopAppBarScrollBehavior {
  if (isSearchScreen || !isCompactDisplay) {
    return TopAppBarDefaults.pinnedScrollBehavior()
  }

  RegFx(id = common.expand_top_app_bar) {
    topAppBarState.heightOffset = 0f
  }
  DisposableEffect(Unit) {
    regEventFx(common.expand_top_app_bar) { _, _ ->
      m(fx to v(v(common.expand_top_app_bar)))
    }
    onDispose { clearEvent(common.expand_top_app_bar) }
  }

  return TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TyTopBar(
  sb: SearchBar?,
  topAppBarScrollBehavior: TopAppBarScrollBehavior
) {
  RegSearchEvents()

  DisposableEffect(Unit) {
    regBounceFx()
    regHttpKtor()
    regPagingFx()

    onDispose {
      clearFx(bounce.fx)
      clearFx(ktor.http_fx)
      clearFx(paging.fx)
    }
  }

  if (sb != null) {
    val topBarScope = rememberCoroutineScope()
    RegCofx(search.coroutine_scope) { cofx ->
      cofx.assoc(search.coroutine_scope, topBarScope)
    }
    TySearchBar(
      searchQuery = sb[searchBar.query] as String,
      onQueryChange = {
        dispatchSync(
          v(
            search.panel_fsm,
            search.update_search_input,
            it
          )
        )
      },
      onSearchClick = {
        dispatch(v(search.panel_fsm, search.submit, it))
      },
      isSearchBarActive = sb[common.state] as Boolean,
      onActiveChange = {
        dispatch(v(search.panel_fsm, search.activate_searchBar))
      },
      onTrailingClick = {
        dispatch(v(search.panel_fsm, search.clear_search_input))
      },
      onLeadingClick = {
        dispatch(v(search.panel_fsm, search.back_press_search))
      },
      suggestions = sb[searchBar.suggestions] as List<String>,
      onSuggestionClick = { selectedSuggestion ->
        // FIXME: Move cursor to the end of text.
        dispatch(
          v(
            search.panel_fsm,
            search.update_search_input,
            selectedSuggestion
          )
        )
      }
    )
  } else {
    val resources: Resources = LocalContext.current.resources
    TyTopAppBar(
      title = "TubeYou",
      actions = watch(query = v(common.top_app_bar_actions, resources)),
      scrollBehavior = topAppBarScrollBehavior
    )
  }
}

@Composable
private fun TyBottomBar(
  navItems: List<TopLevelNavItems>,
  modifier: Modifier = Modifier,
  onClickNavItem: (navItemRoute: String) -> Unit
) {
  val borderColor: Color = MaterialTheme.colorScheme.onSurface.copy(.12f)
  val tint = MaterialTheme.colorScheme.onBackground
  TyNavigationBar(
    modifier = modifier,
    isCompact = true,
    borderColor = borderColor
  ) { itemsModifier ->
    navItems.forEach { navItem ->
      val selected = watch<Boolean>(v(common.is_route_active, navItem.route))
      TyNavigationBarItem(
        selected = selected,
        icon = {
          if (navItem.unselectedIcon is Int) {
            Icon(
              painter = painterResource(navItem.unselectedIcon),
              contentDescription = null,
              tint = tint,
              modifier = Modifier
            )
          } else {
            Icon(
              imageVector = navItem.unselectedIcon as ImageVector,
              contentDescription = null,
              tint = tint,
              modifier = Modifier
            )
          }
        },
        modifier = itemsModifier,
        selectedIcon = {
          if (navItem.selectedIcon is Int) {
            Icon(
              painter = painterResource(navItem.selectedIcon),
              contentDescription = null,
              tint = tint,
              modifier = Modifier.size(32.dp)
            )
          } else {
            Icon(
              imageVector = navItem.selectedIcon as ImageVector,
              contentDescription = null,
              tint = tint,
              modifier = Modifier.size(32.dp)
            )
          }
        },
        label = {
          val type = MaterialTheme.typography
          Text(
            text = stringResource(navItem.label),
            style = if (selected) type.labelMedium else type.labelSmall
          )
        },
        onPressColor = borderColor,
        onClick = { onClickNavItem(navItem.route) }
      )
    }
  }
}

@Composable
private fun screenDimensions(density: Density): Pair<Float, Float> {
  val configuration = LocalConfiguration.current
  return remember(configuration, density) {
    with(density) {
      configuration.screenWidthDp.dp.toPx() to
        configuration.screenHeightDp.dp.toPx()
    }
  }
}

@OptIn(
  ExperimentalLayoutApi::class,
  ExperimentalMaterial3Api::class,
  ExperimentalComposeUiApi::class
)
@Composable
fun TyApp(
  navController: NavHostController = rememberNavController(),
  windowSizeClass: WindowSizeClass,
  appContext: Context
) {
  RegNavFx(navController)
  RegNavCofx(navController)

  RegWatchEvents()
  RegWatchSubs()

  val density = LocalDensity.current
  val activeStreamCache = watch<VideoVm>(v("active_stream_vm"))
  val playbackFsm = watch<IPersistentMap<Any, Any>>(v("stream_panel_fsm"))
  val playbackMachine = get<Any>(playbackFsm, fsm._state)

  val playerSheetRegion =
    get<PlayerSheetState>(playbackMachine, ":player_sheet")

  val view = LocalView.current
  val background = MaterialTheme.colorScheme.background
  LaunchedEffect(isSystemInDarkTheme(), playerSheetRegion) {
    val window = (view.context as Activity).window
    if (playerSheetRegion == PlayerSheetState.EXPANDED) {
      window.statusBarColor = Color.Black.toArgb()
    } else {
      window.statusBarColor = background.toArgb()
    }
  }

  val isPlayerSheetCollapsed = playerSheetRegion == PlayerSheetState.COLLAPSED
  val showThumbnail = get<Boolean>(playbackFsm, "show_player_thumbnail")

  val screenDim = screenDimensions(density)
  val activeStream = watch<UIState>(v("active_stream", appContext, screenDim))

  val orientation = LocalConfiguration.current.orientation

  // WARNING: this must be called before landscape video player
  val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
    bottomSheetState = rememberStandardBottomSheetState(
      initialValue = SheetValue.Hidden,
      skipHiddenState = false
    )
  )

  val isMediaPlaying =
    playerSheetRegion != PlayerSheetState.HIDDEN && playerSheetRegion != null

  val playerScope = rememberCoroutineScope()
  RegCofx("player_scope", playerScope) { cofx ->
    cofx.assoc("player_scope", playerScope)
  }

  if (orientation == ORIENTATION_LANDSCAPE && isMediaPlaying) {
    VideoPlayer(
      modifier = Modifier.padding(start = 24.dp),
      streamState = activeStream,
      useController = !isPlayerSheetCollapsed,
      showThumbnail = showThumbnail,
      thumbnail = activeStreamCache.thumbnail
    )

    return
  }

  val playerSheetState = bottomSheetScaffoldState.bottomSheetState
  val playbackTargetValue = playerSheetState.targetValue

  val onClickClosePlayer: () -> Unit = {
    dispatch(v("stream_panel_fsm", common.close_player))
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.safeDrawing.only(Vertical)),
    bottomBar = {
      if (playbackTargetValue == SheetValue.Expanded) {
        return@Scaffold
      }

      TyBottomBar(
        navItems = topLevelNavItems,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
      ) { navItemRoute ->
        dispatch(v(common.on_click_nav_item, navItemRoute))
      }
    }
  ) { paddingBb ->
    RegSearchSubs()
    val sb = watch<SearchBar?>(v(search.search_bar))
    val topBarState = rememberTopAppBarState()
    val isCompact = isCompact(windowSizeClass)
    val topBarScrollBehavior = topAppBarScrollBehavior(
      topAppBarState = topBarState,
      isSearchScreen = sb != null,
      isCompactDisplay = isCompact
    )

    val playerSheetValue = playerSheetState.currentValue
    LaunchedEffect(playerSheetValue) {
      dispatch(v("stream_panel_fsm", playerSheetValue))
    }
    LaunchedEffect(playbackTargetValue) {
      when {
        playbackTargetValue == SheetValue.Hidden &&
          playerSheetValue != SheetValue.Hidden -> {
          onClickClosePlayer()
        }

        playbackTargetValue == SheetValue.Expanded &&
          playerSheetValue == SheetValue.PartiallyExpanded -> {
          dispatch(v<Any>("stream_panel_fsm", common.expand_player_sheet))
        }
      }
    }
    RegPlayerSheetEffects(playerSheetState)
    val bottomNavBarPadding = paddingBb.calculateBottomPadding()

    BottomSheetScaffold(
      sheetContent = {
        if (orientation == ORIENTATION_LANDSCAPE) {
          return@BottomSheetScaffold
        }

        val sheetPeekHeight = with(density) {
          get<Float>(activeStream.data, ":desc_sheet_height")?.toDp()
            ?: 0.toDp()
        }
        val paddingValues = PaddingValues(bottom = bottomNavBarPadding)
        NowPlayingSheet(
          modifier = Modifier.padding(paddingValues),
          isCollapsed = isPlayerSheetCollapsed,
          onCollapsedClick = {
            dispatch(v<Any>("stream_panel_fsm", common.expand_player_sheet))
          },
          activeStream = activeStream,
          activeStreamCache = activeStreamCache,
          showThumbnail = showThumbnail,
          sheetPeekHeight = sheetPeekHeight,
          onClickClosePlayer = onClickClosePlayer
        )
      },
      modifier = Modifier
        .padding(bottom = bottomNavBarPadding)
        .fillMaxSize()
        .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
        .semantics {
          // Allows to use testTag() for UiAutomator resource-id.
          testTagsAsResourceId = true
        },
      scaffoldState = bottomSheetScaffoldState,
      sheetPeekHeight = remember { MINI_PLAYER_HEIGHT + BOTTOM_BAR_HEIGHT },
      sheetDragHandle = null,
      sheetShape = RoundedCornerShape(0.dp)
    ) {
      Scaffold(
        modifier = Modifier
          .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
          TyTopBar(sb, topBarScrollBehavior)
        }
      ) { paddingTb ->
        val bundle = navController.saveState() ?: Bundle()
        val saveState = rememberSaveable(bundle, saver = NavStateSaver) {
          bundle
        }

        if (!saveState.isEmpty) {
          navController.restoreState(saveState)
        }

        TyNavHost(
          navController = navController,
          modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingTb.calculateTopPadding())
            .consumeWindowInsets(paddingTb)
            .windowInsetsPadding(
              WindowInsets.safeDrawing.only(Horizontal + Vertical)
            ),
          isCompact = isCompact,
          orientation = LocalConfiguration.current.orientation
        )

        // Top-level navigation back handler.
        BackHandler(enabled = watch(v(ty_db.top_level_back_handler_enabled))) {
          dispatch(v(common.back_press_top_nav))
        }

        BackHandler(enabled = sb != null) {
          dispatch(v(search.panel_fsm, search.back_press_search))
        }
      }
    }
  }
}

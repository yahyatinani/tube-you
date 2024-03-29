package io.github.yahyatinani.tubeyou.ui

import android.app.Activity
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Vertical
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.BOTTOM_BAR_HEIGHT
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBarItem
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TySearchBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyTopAppBar
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons
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
import io.github.yahyatinani.recompose.ids.recompose.db
import io.github.yahyatinani.recompose.pagingfx.paging
import io.github.yahyatinani.recompose.pagingfx.regPagingFx
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.R
import io.github.yahyatinani.tubeyou.common.appDbBy
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.tubeyou.common.ty_db.top_level_back_handler_enabled
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.modules.feature.settings.navigation.ABOUT_ROUTE
import io.github.yahyatinani.tubeyou.modules.feature.settings.navigation.aboutScreen
import io.github.yahyatinani.tubeyou.modules.feature.settings.navigation.settingsScreen
import io.github.yahyatinani.tubeyou.modules.feature.settings.screen.SettingsDropdownButton
import io.github.yahyatinani.tubeyou.navigation.MAIN_ROUTE
import io.github.yahyatinani.tubeyou.navigation.RegNavCofx
import io.github.yahyatinani.tubeyou.navigation.RegNavFx
import io.github.yahyatinani.tubeyou.navigation.TopLevelNavItems
import io.github.yahyatinani.tubeyou.navigation.TyNavHost
import io.github.yahyatinani.tubeyou.navigation.mainScreen
import io.github.yahyatinani.tubeyou.navigation.rememberSaveableNavController
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
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen.lerp
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs.RegWatchSubs
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch
import kotlin.enums.EnumEntries
import kotlin.math.roundToInt

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
    val searchBarScope = rememberCoroutineScope()
    RegCofx(search.coroutine_scope) { cofx ->
      cofx.assoc(search.coroutine_scope, searchBarScope)
    }
    TySearchBar(
      searchQuery = sb[searchBar.query] as String,
      onQueryChange = {
        dispatchSync(v(search.panel_fsm, search.update_search_input, it))
      },
      onSearchClick = {
        dispatch(v(search.panel_fsm, search.submit, it))
      },
      isSearchBarActive = sb[common.state] as Boolean,
      onActiveChange = {
        dispatch(v(search.panel_fsm, v(search.activate_searchBar, it)))
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
    TyTopBar(topAppBarScrollBehavior)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TyTopBar(
  topAppBarScrollBehavior: TopAppBarScrollBehavior
) {
  TyTopAppBar(
    title = "TubeYou",
    actions = {
      val colorScheme = MaterialTheme.colorScheme
      val contentDescription = stringResource(
        R.string.top_app_bar_search_action_icon_description
      )
      val tint = colorScheme.onSurface

      IconButton(
        modifier = Modifier.testTag(contentDescription),
        onClick = { dispatch(v(search.panel_fsm, search.show_search_bar)) }
      ) {
        Icon(
          imageVector = TyIcons.Search,
          contentDescription = contentDescription,
          modifier = Modifier.size(30.dp),
          tint = tint
        )
      }

      SettingsDropdownButton(tint = tint)
    },
    scrollBehavior = topAppBarScrollBehavior
  )
}

@Composable
private fun TyBottomBar(
  navItems: EnumEntries<TopLevelNavItems>,
  modifier: Modifier = Modifier,
  onClickNavItem: (navItemRoute: String) -> Unit
) {
  val borderColor: Color = MaterialTheme.colorScheme.outlineVariant
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
          Icon(
            imageVector = navItem.unselectedIcon as ImageVector,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(26.dp)
          )
        },
        modifier = itemsModifier,
        selectedIcon = {
          Icon(
            imageVector = navItem.selectedIcon as ImageVector,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(32.dp)
          )
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

@Composable
private fun screenMask(
  screenHeightPx: Float,
  density: Density,
  bottomSheetOffset: Float,
  playerState: PlayerSheetState?
): Float {
  val third = remember(screenHeightPx) { screenHeightPx / 3f }
  val traverse = remember(third, density) {
    third - with(density) { (56 + 48).dp.toPx() }
  }
  val threshold = remember(screenHeightPx) { screenHeightPx - third }
  return remember(bottomSheetOffset, screenHeightPx, threshold) {
    when {
      playerState == null -> 0f
      bottomSheetOffset < threshold -> .5f
      else -> {
        lerp(
          sheetYOffset = bottomSheetOffset - threshold,
          traverse = traverse,
          start = .5f,
          end = 0f
        )
      }
    }
  }
}

@Composable
fun StatusBarEffect(playerState: PlayerSheetState?) {
  val view = LocalView.current
  val background = MaterialTheme.colorScheme.background
  val isDarkTheme = isSystemInDarkTheme()
  LaunchedEffect(isDarkTheme, playerState) {
    val window = (view.context as Activity).window
    val (statusBarColor, foregroundColorStatusBar) = when (playerState) {
      PlayerSheetState.EXPANDED -> Pair(Color.Black.toArgb(), false)

      else -> Pair(background.toArgb(), !isDarkTheme)
    }
    window.statusBarColor = statusBarColor
    WindowCompat.getInsetsController(window, view).apply {
      isAppearanceLightStatusBars = foregroundColorStatusBar
    }
  }
}

@Composable
@OptIn(
  ExperimentalLayoutApi::class,
  ExperimentalMaterial3Api::class,
  ExperimentalComposeUiApi::class
)
fun TyMain(
  navController: NavHostController,
  appContext: Context,
  windowSizeClass: WindowSizeClass
) {
  RegNavFx(navController)
  RegNavCofx(navController)

  RegWatchEvents()
  RegWatchSubs()

  val density = LocalDensity.current
  val playbackFsm = watch<IPersistentMap<Any, Any>>(v("stream_panel_fsm"))
  val playbackMachine = get<Any>(playbackFsm, fsm._state)
  val playerState = get<PlayerSheetState>(playbackMachine, ":player_sheet")

  StatusBarEffect(playerState)

  val screenDim = screenDimensions(density)
  val activeStream = watch<UIState>(v("active_stream", appContext, screenDim))
  val showThumbnail = get<Boolean>(playbackFsm, "show_player_thumbnail")
  val activeStreamCache = watch<VideoVm>(v("active_stream_vm"))

  val orientation = LocalConfiguration.current.orientation

  // WARNING: this must be called before landscape video player
  val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
    bottomSheetState = rememberStandardBottomSheetState(
      initialValue = SheetValue.Hidden,
      skipHiddenState = false
    )
  )

  val playerScope = rememberCoroutineScope()
  RegCofx("player_scope", playerScope) { cofx ->
    cofx.assoc("player_scope", playerScope)
  }

  val playerBottomSheetExpanded = playerState == PlayerSheetState.EXPANDED
  if (orientation == ORIENTATION_LANDSCAPE && playerBottomSheetExpanded) {
    VideoPlayer(
      modifier = Modifier.padding(start = 24.dp),
      streamState = activeStream,
      showThumbnail = showThumbnail,
      thumbnail = activeStreamCache.thumbnail
    )

    return
  }

  val playerSheetState = bottomSheetScaffoldState.bottomSheetState

  var bottomSheetOffset by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(Unit) {
    playerScope.launch {
      snapshotFlow { playerSheetState.requireOffset() }.collect {
        bottomSheetOffset = it
      }
    }

    playerScope.launch {
      snapshotFlow { playerSheetState.currentValue }.collect {
        dispatch(v("stream_panel_fsm", it))
      }
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.safeDrawing.only(Vertical))
      .semantics {
        // Allows to use testTag() for UiAutomator resource-id.
        testTagsAsResourceId = true
      },
    bottomBar = {
      val offsetY = if (playerState == null) {
        0f
      } else {
        val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
        val bottomBarHeightPx = remember(density) {
          with(density) { BOTTOM_BAR_HEIGHT.toPx() }
        }
        val screenHeightPx = remember(density) {
          with(density) { screenHeightDp.toPx() }
        }
        val bottomHeight = remember(density) {
          with(density) { bottomBarHeightPx + 56.dp.toPx() }
        }

        remember(
          playerState,
          screenHeightPx,
          screenHeightDp,
          bottomSheetOffset
        ) {
          lerp(
            sheetYOffset = bottomSheetOffset,
            traverse = screenHeightPx - bottomHeight,
            start = -bottomBarHeightPx,
            end = 0f
          )
        }
      }

      TyBottomBar(
        navItems = topLevelNavItems,
        modifier = Modifier
          .offset { IntOffset(0, -offsetY.roundToInt()) }
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal)),
        onClickNavItem = { navItemRoute ->
          dispatch(v(common.on_click_nav_item, navItemRoute))
        }
      )
    }
  ) { paddingBb ->
    RegSearchSubs()
    val sb = watch<SearchBar?>(v(search.search_bar))
    val isCompact = isCompact(windowSizeClass)
    val topBarScrollBehavior = topAppBarScrollBehavior(
      topAppBarState = rememberTopAppBarState(),
      isSearchScreen = sb != null,
      isCompactDisplay = isCompact
    )

    RegPlayerSheetEffects(playerSheetState)
    val bottomNavBarPadding = when (playerState) {
      null -> paddingBb.calculateBottomPadding()
      else -> 0.dp
    }
    BottomSheetScaffold(
      sheetContent = {
        val sheetPeekHeight = with(density) {
          get<Float>(activeStream.data, ":desc_sheet_height")?.toDp()
            ?: 0.toDp()
        }

        NowPlayingSheet(
          modifier = Modifier
            .padding(PaddingValues(bottom = bottomNavBarPadding)),
          isCollapsed = playerState == PlayerSheetState.COLLAPSED,
          onCollapsedClick = {
            dispatch(v<Any>("stream_panel_fsm", common.expand_player_sheet))
          },
          activeStream = activeStream,
          activeStreamCache = activeStreamCache,
          showThumbnail = showThumbnail,
          sheetPeekHeight = sheetPeekHeight,
          sheetOffset = { bottomSheetOffset },
          onClickClosePlayer = {
            dispatch(v(common.close_player))
          }
        )
      },
      modifier = Modifier
        .padding(bottom = bottomNavBarPadding)
        .fillMaxSize()
        .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
      scaffoldState = bottomSheetScaffoldState,
      sheetPeekHeight = remember(playerState) {
        if (playerState == null) {
          0.dp
        } else {
          MINI_PLAYER_HEIGHT + BOTTOM_BAR_HEIGHT
        }
      },
      sheetDragHandle = null,
      sheetShape = RoundedCornerShape(0.dp)
    ) {
      Box {
        Scaffold(
          modifier = Modifier
            .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
          topBar = {
            TyTopBar(sb, topBarScrollBehavior)
          }
        ) { paddingTb ->
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
          BackHandler(enabled = watch(v(top_level_back_handler_enabled))) {
            dispatch(v(common.back_press_top_nav))
          }

          BackHandler(enabled = sb != null) {
            dispatch(v(search.panel_fsm, search.back_press_search))
          }

          BackHandler(enabled = playerBottomSheetExpanded) {
            dispatch(v("stream_panel_fsm", common.minimize_player))
          }
        }

        val alpha = screenMask(
          screenHeightPx = screenDim.second,
          density = density,
          bottomSheetOffset = bottomSheetOffset,
          playerState = playerState
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .drawBehind {
              drawRect(Color.Black.copy(alpha = alpha))
            }
        )
      }
    }
  }
}

@Composable
fun TyApp(
  topLevelNavCtrl: NavHostController = rememberSaveableNavController(),
  mainNavController: NavHostController = rememberSaveableNavController(),
  windowSizeClass: WindowSizeClass,
  appContext: Context
) {
  val s = rememberCoroutineScope()
  RegFx("nav_to_about") {
    s.launch { topLevelNavCtrl.navigate(ABOUT_ROUTE) }
  }
  RegFx(":about/nav_back") {
    s.launch { topLevelNavCtrl.popBackStack() }
  }

  regEventFx("on_dropdown_item_about_click") { cofx, _ ->
    m(
      db to appDbBy(cofx).assoc(ty_db.is_top_settings_popup_visible, false),
      fx to v(v("nav_to_about"))
    )
  }
  regEventFx("on_about_nav_ic_click") { _, _ ->
    m(fx to v(v(":about/nav_back")))
  }

  NavHost(
    navController = topLevelNavCtrl,
    startDestination = MAIN_ROUTE,
    modifier = Modifier,
    enterTransition = { slideInVertically { it / 10 } + fadeIn() },
    exitTransition = { slideOutVertically { it / 10 } + fadeOut() }
  ) {
    mainScreen(mainNavController, appContext, windowSizeClass)

    settingsScreen()

    aboutScreen()
  }
}

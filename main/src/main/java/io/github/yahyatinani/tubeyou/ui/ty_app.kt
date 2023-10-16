package io.github.yahyatinani.tubeyou.ui

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import io.github.yahyatinani.tubeyou.modules.feature.settings.navigation.ABOUT_ROUTE
import io.github.yahyatinani.tubeyou.modules.feature.settings.navigation.aboutScreen
import io.github.yahyatinani.tubeyou.modules.feature.settings.navigation.settingsScreen
import io.github.yahyatinani.tubeyou.modules.feature.settings.screen.SettingsDropdownButton
import io.github.yahyatinani.tubeyou.navigation.MAIN_ROUTE
import io.github.yahyatinani.tubeyou.navigation.RegNavCofx
import io.github.yahyatinani.tubeyou.navigation.RegNavFx
import io.github.yahyatinani.tubeyou.navigation.TyNavHost
import io.github.yahyatinani.tubeyou.navigation.mainScreen
import io.github.yahyatinani.tubeyou.navigation.rememberSaveableNavController
import io.github.yahyatinani.tubeyou.navigation.topLevelNavItems
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.db.SearchBar
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.evnts.RegSearchEvents
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.subs.RegSearchSubs
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.events.RegWatchEvents
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen.MINI_PLAYER_HEIGHT
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen.NowPlayingScaffold
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen.lerp
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs.RegWatchSubs
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TyTopBar(
  topSearchBar: SearchBar?,
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

  if (topSearchBar != null) {
    val searchBarScope = rememberCoroutineScope()
    RegCofx(search.coroutine_scope) { cofx ->
      cofx.assoc(search.coroutine_scope, searchBarScope)
    }
    TySearchBar(
      searchQuery = topSearchBar[searchBar.query] as String,
      onQueryChange = {
        dispatchSync(v(search.panel_fsm, search.update_search_input, it))
      },
      onSearchClick = {
        dispatch(v(search.panel_fsm, search.submit, it))
      },
      isSearchBarActive = topSearchBar[common.state] as Boolean,
      onActiveChange = {
        dispatch(v(search.panel_fsm, v(search.activate_searchBar, it)))
      },
      onTrailingClick = {
        dispatch(v(search.panel_fsm, search.clear_search_input))
      },
      onLeadingClick = {
        dispatch(v(search.panel_fsm, search.back_press_search))
      },
      suggestions = topSearchBar[searchBar.suggestions] as List<String>,
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

@Composable
private fun TyBottomBar(
  modifier: Modifier = Modifier,
  onClickNavItem: (navItemRoute: String) -> Unit,
  sheetOffset: () -> Float
) {
  val density = LocalDensity.current
  val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
  val bottomBarHeightPx = remember(density) {
    with(density) { BOTTOM_BAR_HEIGHT.toPx() }
  }
  val screenHeightPx = remember(density, screenHeightDp) {
    with(density) { screenHeightDp.toPx() }
  }
  val bottomHeight = remember(density) {
    with(density) { bottomBarHeightPx + 56.dp.toPx() }
  }
  val colorScheme = MaterialTheme.colorScheme
  val borderColor: Color = colorScheme.outlineVariant
  TyNavigationBar(
    modifier = modifier
      .graphicsLayer {
        translationY = -lerp(
          sheetYOffset = sheetOffset(),
          traverse = screenHeightPx - bottomHeight,
          start = -bottomBarHeightPx,
          end = 0f
        )
      },
    isCompact = true,
    borderColor = borderColor
  ) { itemsModifier ->
    val tint = colorScheme.onBackground
    topLevelNavItems.forEach { navItem ->
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
private fun screenDimensions(): Pair<Float, Float> {
  val density = LocalDensity.current
  val configuration = LocalConfiguration.current
  return remember(configuration, density) {
    with(density) {
      configuration.screenWidthDp.dp.toPx() to
        configuration.screenHeightDp.dp.toPx()
    }
  }
}

fun lerpRGB(from: Color, to: Color, t: Float): Color = Color(
  red = from.red + (to.red - from.red) * t,
  green = from.green + (to.green - from.green) * t,
  blue = from.blue + (to.blue - from.blue) * t,
  alpha = from.alpha + (to.alpha - from.alpha) * t
)

@Composable
fun StatusBarColorEffect(screenHeightPx: Float, sheetOffset: () -> Float) {
  val view = LocalView.current
  val colorScheme = MaterialTheme.colorScheme
  val background = colorScheme.background
  val isDarkTheme = isSystemInDarkTheme()

  val density = LocalDensity.current
  val minValue = remember {
    with(density) {
      (MINI_PLAYER_HEIGHT + BOTTOM_BAR_HEIGHT).toPx()
    }
  }
  val maxValue = remember(screenHeightPx) { screenHeightPx - minValue }
  val n = (sheetOffset() - minValue) / (maxValue - minValue)
  LaunchedEffect(isDarkTheme, n) {
    val color = lerpRGB(Color.Black, background, n.coerceIn(0f, 1f))
    val window = (view.context as Activity).window
    window.statusBarColor = color.toArgb()
    WindowCompat.getInsetsController(window, view).apply {
      isAppearanceLightStatusBars =
        if (color == Color.Black) false else !isDarkTheme
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
  RegWatchEvents()
  RegWatchSubs()

  val screenDim = screenDimensions()
  val density = LocalDensity.current
  val cfg = LocalConfiguration.current
  val nowPlayingStream =
    watch<UIState?>(v(":now_playing_stream", appContext, cfg, density))

  // WARNING: this must be called before landscape video player
  val bottomSheetState = rememberStandardBottomSheetState(
    initialValue = SheetValue.Hidden,
    skipHiddenState = false
  )
  val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
    bottomSheetState = bottomSheetState
  )

  /*
    val orientation = LocalConfiguration.current.orientation
    if (orientation == ORIENTATION_LANDSCAPE &&
      playerState == PlayerSheetState.EXPANDED
    ) {
      VideoPlayer(
        modifier = Modifier.padding(start = 24.dp),
        streamState = activeStream,
        showThumbnail = showThumbnail,
        thumbnail = activeStreamCache.thumbnail
      )

      return
    }*/

  var bottomSheetOffset by remember { mutableFloatStateOf(screenDim.second) }

  val playerScope = rememberCoroutineScope()
  RegCofx(id = "player_scope", key1 = playerScope) { cofx ->
    cofx.assoc("player_scope", playerScope)
  }

  LaunchedEffect(Unit) {
    snapshotFlow { bottomSheetState.requireOffset() }.collect {
      bottomSheetOffset = it
    }
  }

  LaunchedEffect(Unit) {
    snapshotFlow { bottomSheetState.currentValue }.collect {
      dispatch(v("stream_panel_fsm", v("now_playing_sheet", it)))
    }
  }

  StatusBarColorEffect(
    screenHeightPx = screenDim.second,
    sheetOffset = { bottomSheetOffset }
  )

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.safeDrawing.only(Vertical))
      .semantics {
        // Allows to use testTag() for UiAutomator resource-id.
        testTagsAsResourceId = true
      },
    bottomBar = {
      TyBottomBar(
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal)),
        onClickNavItem = { navItemRoute ->
          dispatch(v(common.on_click_nav_item, navItemRoute))
        },
        sheetOffset = { bottomSheetOffset }
      )
    }
  ) { padding ->
    RegSearchSubs()
    val sb = watch<SearchBar?>(v(search.search_bar))
    val isCompact = isCompact(windowSizeClass)
    val topBarScrollBehavior = topAppBarScrollBehavior(
      topAppBarState = rememberTopAppBarState(),
      isSearchScreen = sb != null,
      isCompactDisplay = isCompact
    )

    val third = remember(key1 = screenDim.second) { screenDim.second / 3f }
    val threshold = remember(third) { screenDim.second - third }
    val traverse = remember(third, density) {
      third - with(density) {
        (MINI_PLAYER_HEIGHT + BOTTOM_BAR_HEIGHT).toPx()
      }
    }

    NowPlayingScaffold(
      modifier = Modifier
        .drawWithContent {
          drawContent()
          val offset = bottomSheetOffset
          drawRect(
            color = Color.Black,
            alpha = when {
              offset < threshold -> .5f
              else -> lerp(
                sheetYOffset = offset - threshold,
                traverse = traverse,
                start = .5f,
                end = 0f
              )
            }
          )
        }
        .fillMaxSize()
        .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
      nowPlayingStream = nowPlayingStream,
      scaffoldState = bottomSheetScaffoldState,
      bottomSheetOffset = { bottomSheetOffset }
    ) {
      Scaffold(
        topBar = {
          TyTopBar(
            topSearchBar = sb,
            topAppBarScrollBehavior = topBarScrollBehavior
          )
        }
      ) { paddingTb ->
        RegNavFx(navController)
        RegNavCofx(navController)
        TyNavHost(
          navController = navController,
          modifier = Modifier
            .fillMaxSize()
            .padding(
              top = paddingTb.calculateTopPadding(),
              bottom = padding.calculateBottomPadding()
            )
            .consumeWindowInsets(paddingTb)
            .windowInsetsPadding(
              WindowInsets.safeDrawing.only(Horizontal + Vertical)
            ),
          isCompact = isCompact,
          orientation = cfg.orientation
        )

        // Top-level navigation back handler.
        BackHandler(enabled = watch(v(top_level_back_handler_enabled))) {
          dispatch(v(common.back_press_top_nav))
        }

        BackHandler(enabled = sb != null) {
          dispatch(v(search.panel_fsm, search.back_press_search))
        }

        BackHandler(
          enabled = bottomSheetState.currentValue == SheetValue.Expanded
        ) {
          dispatch(v<Any>("stream_panel_fsm", common.minimize_player))
        }
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

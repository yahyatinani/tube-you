package com.github.yahyatinani.tubeyou

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.dispatchSync
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.recompose.regFx
import com.github.whyrising.recompose.watch
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_navigation_item
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
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBar
import com.github.yahyatinani.tubeyou.modules.panel.home.homeGraph
import com.github.yahyatinani.tubeyou.modules.panel.library.libraryGraph
import com.github.yahyatinani.tubeyou.modules.panel.subscriptions.subsGraph

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
    val topBarState = rememberTopAppBarState()
    val sb = watch<SearchBar?>(v(search.search_bar))
    val scrollBehavior =
      topAppBarScrollBehavior(isCompactSize, topBarState, sb)
    Scaffold(
      modifier = Modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection)
        .semantics {
          // Allows to use testTag() for UiAutomator resource-id.
          testTagsAsResourceId = true
        },
      topBar = {
        when {
          sb != null -> {
            val scope = rememberCoroutineScope()
            regCofx(search.coroutine_scope) { cofx ->
              cofx.assoc(search.coroutine_scope, scope)
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
              dispatchSync(v(search.panel_fsm, search.back_press_search))
            }
          }

          else -> {
            TopAppBar(
              modifier = Modifier
                .windowInsetsPadding(
                  insets = WindowInsets.safeDrawing.only(Top + Horizontal)
                )
                .padding(end = if (isCompactSize) 4.dp else 16.dp),
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
      },
      bottomBar = {
        TyBottomNavigationBar(
          navItems = watch(v(common.navigation_items)),
          isCompact = isCompactSize,
          colorScheme = colorScheme
        ) { dispatch(v(common.on_click_nav_item, it)) }
      }
    ) {
      val enabled = !watch<Boolean>(query = v(common.is_backstack_empty))
      BackHandler(enabled) { dispatchSync(v(common.bottom_bar_back_press)) }

      val orientation = LocalConfiguration.current.orientation
      val thumbnailHeight = thumbnailHeight(orientation)
      NavHost(
        navController = navController,
        startDestination = HOME_GRAPH_ROUTE,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
          .padding(it)
          .consumeWindowInsets(it)
      ) {
        homeGraph(isCompactSize, orientation, thumbnailHeight)
        subsGraph(isCompactSize, orientation, thumbnailHeight)
        libraryGraph(isCompactSize, orientation, thumbnailHeight)
      }
    }
  }
}

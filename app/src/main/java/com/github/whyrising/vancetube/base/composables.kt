package com.github.whyrising.vancetube.base

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize.Companion.Zero
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.fx.FxIds
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.recompose.regFx
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.base.base.bottom_nav_items
import com.github.whyrising.vancetube.base.base.expand_top_app_bar
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.vancetube.home.homeLarge
import com.github.whyrising.vancetube.library.library
import com.github.whyrising.vancetube.subscriptions.subscriptions
import com.github.whyrising.vancetube.trends.trending
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.vancetube.ui.theme.composables.BackArrow
import com.github.whyrising.vancetube.ui.theme.isCompact
import com.github.whyrising.y.core.getFrom
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

const val ANIM_OFFSET_X = 300

@Composable
private fun DestinationTrackingSideEffect(navController: NavHostController) {
  DisposableEffect(navController) {
    val listener = NavController
      .OnDestinationChangedListener { _, navDestination, _ ->
        navDestination.route.let {
          if (it != null) {
            dispatch(v(base.on_bottom_nav_click, it))
          }
        }
        // FIXME: use this when a new activity on top
//        val previousBackStackEntry = navCtrl.previousBackStackEntry
//        if (previousBackStackEntry != null) {
//          val route = previousBackStackEntry.destination.route
//          if (route != null) dispatch(v(base.current_bottom_nav_panel, route))
//        }
//        dispatch(v(base.set_backstack_status, flag))
      }

    navController.addOnDestinationChangedListener(listener)

    onDispose {
      navController.removeOnDestinationChangedListener(listener)
    }
  }
}

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalComposeUiApi::class,
  ExperimentalAnimationApi::class,
  ExperimentalLayoutApi::class
)
@Composable
fun VanceApp(
  windowSizeClass: WindowSizeClass,
  navController: NavHostController = rememberAnimatedNavController()
) {
  DestinationTrackingSideEffect(navController)

  LaunchedEffect(Unit) {
    regBaseFx(navController)
    regBaseEventHandlers
  }
  regBaseSubs

  val isCompactDisplay = isCompact(windowSizeClass)
  VanceTheme(isCompact = isCompactDisplay) {
    val scrollBehavior = when {
      isCompactDisplay -> {
        val topAppBarState = rememberTopAppBarState()
        LaunchedEffect(Unit) {
          regFx(expand_top_app_bar) {
            topAppBarState.heightOffset = 0f
          }
          regEventFx(expand_top_app_bar) { _, _ ->
            m(FxIds.fx to v(v(expand_top_app_bar)))
          }
        }
        enterAlwaysScrollBehavior(topAppBarState)
      }
      else -> pinnedScrollBehavior()
    }
    Scaffold(
      modifier = Modifier
        .then(
          if (isCompactDisplay) {
            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
          } else Modifier
        )
        .semantics {
          // Allows to use testTag() for UiAutomator resource-id.
          testTagsAsResourceId = true
        },
      topBar = {
        TopAppBar(
          modifier = Modifier.windowInsetsPadding(
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top + Horizontal)
          ),
          title = {
            IconButton(onClick = { /*TODO*/ }) {
              Icon(
                imageVector = Icons.Outlined.Search,
                modifier = Modifier.size(26.dp),
                contentDescription = "Search a video"
              )
            }
          },
          scrollBehavior = scrollBehavior,
          navigationIcon = {
            if (subscribe<Boolean>(v(base.is_backstack_available)).w()) {
              BackArrow()
            }
          },
          actions = {
            IconButton(onClick = { /*TODO*/ }) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "more"
              )
            }
          }
        )
      },
      bottomBar = {
        Surface(color = MaterialTheme.colorScheme.surface) {
          Column(
            modifier = Modifier.windowInsetsPadding(
              WindowInsets.safeDrawing.only(Horizontal + Bottom)
            )
          ) {
            Divider(
              color = MaterialTheme.colorScheme.onSurface.copy(.15f),
              thickness = .6.dp
            )
            NavigationBar(
              containerColor = Color.Transparent,
              modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(Horizontal + Bottom)
              )
            ) {
              subscribe<Map<Any, Any>>(v(bottom_nav_items)).w()
                .forEach { (route, navItem) ->
                  NavigationBarItem(
                    selected = getFrom(navItem, base.is_selected)!!,
                    icon = {
                      Icon(
                        imageVector = getFrom(navItem, base.icon)!!,
                        contentDescription = stringResource(
                          getFrom(navItem, base.icon_content_desc_text_id)!!
                        ),
                        tint = MaterialTheme.colorScheme.onBackground
                      )
                    },
                    label = {
                      Text(
                        text = stringResource(
                          getFrom(navItem, base.label_text_id)!!
                        ),
                        style = MaterialTheme.typography.labelSmall
                      )
                    },
                    onClick = { dispatch(v(base.navigate_to, route)) }
                  )
                }
            }
          }
        }
      }
    ) {
      val orientation = LocalConfiguration.current.orientation
      AnimatedNavHost(
        navController = navController,
        startDestination = subscribe<String>(v(base.start_route)).w(),
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
          .padding(it)
          .consumedWindowInsets(it)
      ) {
        if (isCompactDisplay) {
          home(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
          trending(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
          subscriptions(animOffsetX = ANIM_OFFSET_X, orientation = orientation)
          library(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
        } else {
          homeLarge(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
          trending(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
          subscriptions(animOffsetX = ANIM_OFFSET_X, orientation = orientation)
          library(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
        }
      }
    }
  }
}

// -- Previews -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun BasePanelPreview() {
  initAppDb()
  VanceApp(windowSizeClass = WindowSizeClass.calculateFromSize(Zero))
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BasePanelDarkPreview() {
  BasePanelPreview()
}

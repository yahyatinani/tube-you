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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.base.base.bottom_nav_items
import com.github.whyrising.vancetube.base.db.NavigationItemState
import com.github.whyrising.vancetube.base.db.initAppDb
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.vancetube.home.homeLarge
import com.github.whyrising.vancetube.ui.anim.enterAnimation
import com.github.whyrising.vancetube.ui.anim.exitAnimation
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.vancetube.ui.theme.composables.BackArrow
import com.github.whyrising.vancetube.ui.theme.isCompact
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalComposeUiApi::class,
  ExperimentalAnimationApi::class,
  ExperimentalLayoutApi::class
)
@Composable
fun VanceApp(windowSizeClass: WindowSizeClass) {
  val navController = rememberAnimatedNavController()
  DisposableEffect(navController) {
    val listener = NavController
      .OnDestinationChangedListener { navCtrl, navDestination, _ ->
        val route = navDestination.route
        if (route != null) {
          dispatch(v(base.select_bottom_nav_item, route))
        }
        // FIXME: use this when a new activity on top
//        val flag = navCtrl.previousBackStackEntry != null
//        dispatch(v(base.set_backstack_status, flag))
      }

    navController.addOnDestinationChangedListener(listener)

    onDispose {
      navController.removeOnDestinationChangedListener(listener)
    }
  }
  regBaseFx(navController)
  VanceTheme(windowSizeClass = windowSizeClass) {
    val isCompactDisplay = remember { isCompact(windowSizeClass) }
    val scrollBehavior = when {
      isCompactDisplay -> TopAppBarDefaults.enterAlwaysScrollBehavior()
      else -> TopAppBarDefaults.pinnedScrollBehavior()
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
                modifier = Modifier.size(32.dp),
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
              modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(Horizontal + Bottom)
              ),
              containerColor = Color.Transparent
            ) {
              val navItems =
                subscribe<List<NavigationItemState>>(v(bottom_nav_items)).w()
              navItems.forEach {
                NavigationBarItem(
                  selected = it.isSelected,
                  icon = {
                    Icon(
                      imageVector = it.icon,
                      contentDescription = stringResource(it.contentDescTextId),
                      tint = MaterialTheme.colorScheme.onBackground
                    )
                  },
                  label = {
                    Text(
                      text = stringResource(it.labelTextId),
                      style = MaterialTheme.typography.labelSmall
                    )
                  },
                  onClick = { dispatch(v(base.navigate_to, it)) }
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
        startDestination = NavigationItemState.Home.route,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
          .padding(it)
          .consumedWindowInsets(it)
      ) {
        when {
          isCompactDisplay -> {
            home(
              animOffSetX = 300,
              orientation = orientation
            )
          }
          else -> homeLarge(
            animOffSetX = 300,
            orientation = orientation
          )
        }

        composable(
          route = NavigationItemState.Subscriptions.route,
          exitTransition = { exitAnimation(targetOffsetX = -300) },
          popEnterTransition = { enterAnimation(initialOffsetX = -300) }
        ) {
          Surface(modifier = Modifier.fillMaxSize()) {
            Text(text = "TODO: subs")
          }
        }
        composable(
          route = NavigationItemState.Library.route,
          exitTransition = { exitAnimation(targetOffsetX = -300) },
          popEnterTransition = { enterAnimation(initialOffsetX = -300) }
        ) {
          Surface(modifier = Modifier.fillMaxSize()) {
            Text(text = "TODO: library")
          }
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

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BasePanelDarkPreview() {
  initAppDb()
  VanceApp(windowSizeClass = WindowSizeClass.calculateFromSize(Zero))
}

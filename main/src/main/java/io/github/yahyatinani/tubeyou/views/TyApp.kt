package io.github.yahyatinani.tubeyou.views

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.top_level_back_handler_enabled
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBarItem
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import io.github.yahyatinani.recompose.cofx.regCofx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.db.navItems
import io.github.yahyatinani.tubeyou.fx.BackStack
import io.github.yahyatinani.tubeyou.fx.BackStack.queue
import io.github.yahyatinani.tubeyou.fx.RegNavFx
import io.github.yahyatinani.tubeyou.fx.currentDestination
import io.github.yahyatinani.tubeyou.navigation.TopLevelNavItems
import io.github.yahyatinani.tubeyou.navigation.TyNavHost
import io.github.yahyatinani.y.core.v

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TyApp(navController: NavHostController = rememberNavController()) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      TyBottomBar(
        navItems = navItems,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
      ) { navItemRoute ->
        dispatch(v(common.on_click_nav_item, navItemRoute))
      }
    }
  ) { padding ->
    RegNavFx(navController)
    LaunchedEffect(Unit) {
      regCofx(common.start_destination) { cofx ->
        cofx.assoc(
          common.start_destination,
          navController.graph.findStartDestination().id
        )
      }
      regCofx(common.prev_top_nav_route) { cofx ->
        val route = BackStack.pop(currentDestination(navController))
        cofx.assoc(common.prev_top_nav_route, route)
      }
      regCofx(common.is_top_backstack_empty) { cofx ->
        queue.forEach { print("$it,") }
        cofx.assoc(common.is_top_backstack_empty, queue.size <= 1)
      }
    }

    TyNavHost(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .consumeWindowInsets(padding)
        .windowInsetsPadding(
          WindowInsets.safeDrawing.only(Horizontal + Vertical)
        ),
      navController = navController
    )

    BackHandler(
      enabled = watch(v(top_level_back_handler_enabled))
    ) {
      dispatch(v(common.back_press_top_nav))
    }
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
          Icon(
            painter = painterResource(navItem.unselectedIcon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier
          )
        },
        modifier = itemsModifier,
        selectedIcon = {
          Icon(
            painter = painterResource(navItem.selectedIcon),
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

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun TyAppPreview() {
  TyTheme {
    TyApp()
  }
}

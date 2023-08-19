package io.github.yahyatinani.tubeyou.ui

import android.content.res.Resources
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBarItem
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyTopAppBar
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.tubeyou.navigation.RegNavCofx
import io.github.yahyatinani.tubeyou.navigation.RegNavFx
import io.github.yahyatinani.tubeyou.navigation.TopLevelNavItems
import io.github.yahyatinani.tubeyou.navigation.TyNavHost
import io.github.yahyatinani.tubeyou.navigation.topLevelNavItems
import io.github.yahyatinani.y.core.v

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TyApp(navController: NavHostController = rememberNavController()) {
  RegNavFx(navController)
  RegNavCofx(navController)

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      TyBottomBar(
        navItems = topLevelNavItems,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
      ) { navItemRoute ->
        dispatch(v(common.on_click_nav_item, navItemRoute))
      }
    }
  ) { paddingBb ->
    Scaffold(
      modifier = Modifier.padding(bottom = paddingBb.calculateBottomPadding()),
      topBar = {
        // TODO: scrollable top bar.
        val r: Resources = LocalContext.current.resources
        TyTopAppBar(
          title = "TubeYou",
          actions = watch(query = v(common.top_app_bar_actions, r))
        )
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
          )
      )

      // Top-level navigation back handler.
      BackHandler(
        enabled = watch(v(ty_db.top_level_back_handler_enabled))
      ) {
        dispatch(v(common.back_press_top_nav))
      }
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

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun TyAppPreview() {
  TyTheme {
    TyApp()
  }
}

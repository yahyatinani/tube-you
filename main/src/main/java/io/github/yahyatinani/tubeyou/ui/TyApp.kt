package io.github.yahyatinani.tubeyou.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyNavigationBarItem
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import io.github.yahyatinani.tubeyou.navigation.MainNavItems

@Composable
private fun TyBottomBar(navItems: List<MainNavItems>) {
  val borderColor: Color = MaterialTheme.colorScheme.onSurface.copy(.12f)

  TyNavigationBar(
    isCompact = true,
    borderColor = borderColor
  ) { itemsModifier ->
    val colorScheme = MaterialTheme.colorScheme
    navItems.forEach { navItem ->
      val selected = false
      TyNavigationBarItem(
        selected = selected,
        icon = {
          Icon(
            painter = painterResource(navItem.unselectedIcon),
            contentDescription = null,
            tint = colorScheme.onBackground,
            modifier = Modifier
          )
        },
        modifier = itemsModifier,
        selectedIcon = {
          Icon(
            painter = painterResource(navItem.selectedIcon),
            contentDescription = null,
            tint = colorScheme.onBackground,
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
        onClick = { /*TODO navItem.route */ }
      )
    }
  }
}

@Composable
fun TyApp(modifier: Modifier = Modifier) {
  Scaffold(
    bottomBar = {
      val navItems = MainNavItems.values().asList()
      TyBottomBar(navItems = navItems)
    }
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(it),
      color = MaterialTheme.colorScheme.background
    ) {
      Text(
        text = "Hello Android!",
        modifier = modifier
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

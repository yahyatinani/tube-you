package com.github.whyrising.vancetube.base

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.initAppDb
import com.github.whyrising.vancetube.ui.theme.BackArrow
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.y.core.v

@Composable
fun BasePanel(
  backgroundColor: Color = MaterialTheme.colors.background,
  content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBar(
        elevation = 0.dp,
        backgroundColor = backgroundColor,
        title = {
          IconButton(onClick = { /*TODO*/ }) {
            Icon(
              imageVector = Icons.Outlined.Search,
              modifier = Modifier.size(32.dp),
              contentDescription = "Search a video",
            )
          }
        },
        navigationIcon = when {
          subscribe<Boolean>(v(base.is_backstack_available)).w() -> {
            { BackArrow() }
          }
          else -> null
        }
      )
    },
    bottomBar = {
      BottomNavigation(
        backgroundColor = backgroundColor,
        elevation = 0.dp,
      ) {
        BottomNavigationItem(
          selected = true,
          onClick = { /*TODO*/ },
          label = {
            Text(text = "Home")
          },
          icon = {
            Icon(
              imageVector = Icons.Filled.Home,
              contentDescription = "Home panel"
            )
          }
        )
        BottomNavigationItem(
          selected = false,
          onClick = { /*TODO*/ },
          label = {
            Text(text = "Library")
          },
          icon = {
            Icon(
              imageVector = Icons.Outlined.PlayArrow,
              contentDescription = "Library panel"
            )
          }
        )
      }
    }
  ) { paddingValues ->
    content(paddingValues)
  }
}

// -- Previews -----------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun BasePanelPreview() {
  initAppDb()
  regBaseEventHandlers()
  regBaseSubs()
  VanceTheme {
    BasePanel {}
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BasePanelDarkPreview() {
  VanceTheme {
    BasePanel {}
  }
}

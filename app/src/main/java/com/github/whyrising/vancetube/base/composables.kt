package com.github.whyrising.vancetube.base


import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.List
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
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.vancetube.ui.theme.composables.BackArrow
import com.github.whyrising.vancetube.ui.theme.composables.CustomBottomNavigation
import com.github.whyrising.vancetube.ui.theme.composables.SmallLabelText
import com.github.whyrising.vancetube.ui.theme.composables.VanceBottomNavigationItem
import com.github.whyrising.y.core.v

@Composable
fun BottomNavigationBar(backgroundColor: Color) {
  CustomBottomNavigation(
    backgroundColor = backgroundColor,
    elevation = 0.dp,
  ) {
    VanceBottomNavigationItem(
      selected = true,
      onClick = { /*TODO*/ },
      label = { SmallLabelText(text = "Home") },
      icon = {
        Icon(
          imageVector = Icons.Filled.Home,
          contentDescription = "Home panel",
          tint = MaterialTheme.colors.onBackground
        )
      }
    )
    VanceBottomNavigationItem(
      selected = false,
      onClick = { /*TODO*/ },
      label = { SmallLabelText(text = "Subscriptions") },
      icon = {
        Icon(
          imageVector = Icons.Outlined.PlayArrow,
          contentDescription = "Library panel"
        )
      }
    )
    VanceBottomNavigationItem(
      selected = false,
      onClick = { /*TODO*/ },
      label = { SmallLabelText(text = "Library") },
      icon = {
        Icon(
          imageVector = Icons.Outlined.List,
          contentDescription = "Library panel"
        )
      }
    )
  }
}

@Composable
fun BasePanel(
  backgroundColor: Color = MaterialTheme.colors.background,
  content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBar(
        modifier = Modifier.height(48.dp),
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
      BottomNavigationBar(backgroundColor)
    }
  ) { paddingValues ->
    content(paddingValues)
  }
}

// -- Previews -----------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
  VanceTheme {
    BottomNavigationBar(backgroundColor = MaterialTheme.colors.background)
  }
}

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

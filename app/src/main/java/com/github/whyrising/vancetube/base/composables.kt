package com.github.whyrising.vancetube.base

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize.Companion.Zero
import androidx.compose.ui.unit.dp
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.initAppDb
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.vancetube.ui.theme.composables.BackArrow
import com.github.whyrising.y.core.v

fun LazyListState.canBeScrolled(): State<Boolean> = derivedStateOf {
  val layoutInfo = layoutInfo
  val visibleItemsInfo = layoutInfo.visibleItemsInfo

  if (layoutInfo.totalItemsCount == 0) {
    false
  } else {
    val firstVisibleItem = visibleItemsInfo.first()
    val lastVisibleItem = visibleItemsInfo.last()

    val viewportHeight =
      layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

    !(
      firstVisibleItem.index == 0 &&
        firstVisibleItem.offset == 0 &&
        lastVisibleItem.index + 1 == layoutInfo.totalItemsCount &&
        lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight
      )
  }
}

@OptIn(
  ExperimentalMaterial3WindowSizeClassApi::class,
  ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun BasePanel(
  backgroundColor: Color = MaterialTheme.colorScheme.background,
  windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(Zero),
  content: @Composable (PaddingValues) -> Unit
) {
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
  Scaffold(
    modifier = Modifier
      .nestedScroll(scrollBehavior.nestedScrollConnection)
      .semantics {
        // Allows to use testTag() for UiAutomator resource-id.
        testTagsAsResourceId = true
      },
    topBar = {
      TopAppBar(
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
          if (subscribe<Boolean>(v(base.is_backstack_available)).w())
            BackArrow()
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
      Column {
        Divider(
          color = MaterialTheme.colorScheme.onSurface.copy(.15f),
          thickness = .6.dp
        )
        NavigationBar(
          modifier = Modifier,
          containerColor = MaterialTheme.colorScheme.background
        ) {
          val style = MaterialTheme.typography.labelSmall
          NavigationBarItem(
            modifier = Modifier,
            selected = true,
            icon = {
              Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home panel",
                tint = MaterialTheme.colorScheme.onBackground
              )
            },
            label = {
              Text(text = "Home", style = style)
            },
            onClick = {},
          )
          NavigationBarItem(
            selected = false,
            icon = {
              Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = "Subscriptions panel"
              )
            },
            label = {
              Text(text = "Subscriptions", style = style)
            },
            onClick = {},
          )
          NavigationBarItem(
            selected = false,
            icon = {
              Icon(
                imageVector = Icons.Outlined.List,
                contentDescription = "Library panel"
              )
            },
            label = {
              Text(text = "Library", style = style)
            },
            onClick = {},
          )
        }
      }
    }
  ) {
    content(it)
  }
}

// -- Previews -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
  VanceTheme {
//    BottomNavigationBar(
//      backgroundColor = MaterialTheme.colorScheme.background,
//      windowSizeClass = WindowSizeClass.calculateFromSize(Zero)
//    )
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

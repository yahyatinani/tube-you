package com.github.whyrising.vancetube.base

import VanceScaffold
import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize.Companion.Zero
import androidx.compose.ui.unit.IntOffset
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
import kotlin.math.roundToInt

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

@Composable
fun BottomNavigationBar(
  backgroundColor: Color,
  windowSizeClass: WindowSizeClass
) {
  CustomBottomNavigation(
    backgroundColor = backgroundColor,
    elevation = 0.dp,
    windowSizeClass = windowSizeClass
  ) {
    VanceBottomNavigationItem(
      selected = true,
      onClick = { /*TODO*/ },
      label = {
        SmallLabelText(text = "Home")
      },
      icon = {
        Icon(
          imageVector = Icons.Filled.Home,
          contentDescription = "Home panel",
          tint = MaterialTheme.colors.onBackground
        )
      },
      windowSizeClass = windowSizeClass
    )

    VanceBottomNavigationItem(
      selected = false,
      onClick = { /*TODO*/ },
      label = {
        SmallLabelText(text = "Subscriptions")
      },
      icon = {
        Icon(
          imageVector = Icons.Outlined.PlayArrow,
          contentDescription = "Library panel"
        )
      },
      windowSizeClass = windowSizeClass
    )

    VanceBottomNavigationItem(
      selected = false,
      onClick = { /*TODO*/ },
      label = {
        SmallLabelText(text = "Library")
      },
      icon = {
        Icon(
          imageVector = Icons.Outlined.List,
          contentDescription = "Library panel"
        )
      },
      windowSizeClass = windowSizeClass
    )
  }
}

val TOP_APP_BAR_HEIGHT = 48.dp

@OptIn(
  ExperimentalMaterial3WindowSizeClassApi::class,
  ExperimentalComposeUiApi::class
)
@Composable
fun BasePanel(
  backgroundColor: Color = MaterialTheme.colors.background,
  windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(Zero),
  content: @Composable (PaddingValues) -> Unit
) {
  val topBarHeightPx = with(LocalDensity.current) {
    TOP_APP_BAR_HEIGHT.roundToPx().toFloat()
  }
  val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
  val nestedScrollConnection = remember {
    object : NestedScrollConnection {
      override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
      ): Offset {
        val delta = available.y
        val newOffset = toolbarOffsetHeightPx.value + delta
        toolbarOffsetHeightPx.value = newOffset.coerceIn(-topBarHeightPx, 0f)
        return Offset.Zero
      }
    }
  }

  VanceScaffold(
    modifier = Modifier
      .semantics {
        // Allows to use testTag() for UiAutomator resource-id.
        testTagsAsResourceId = true
      }
      .then(
        when {
          subscribe<Boolean>(v(base.is_top_bar_fixed)).w() -> Modifier
          else -> Modifier.nestedScroll(nestedScrollConnection)
        }
      ),
    topBar = {
      TopAppBar(
        modifier = Modifier
          .height(TOP_APP_BAR_HEIGHT)
          .offset {
            IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt())
          },
        elevation = 0.dp,
        backgroundColor = backgroundColor,
        title = {
          IconButton(onClick = { /*TODO*/ }) {
            Icon(
              imageVector = Icons.Outlined.Search,
              modifier = Modifier.size(32.dp),
              contentDescription = "Search a video"
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
      BottomNavigationBar(backgroundColor, windowSizeClass)
    }
  ) {
    content(PaddingValues(top = TOP_APP_BAR_HEIGHT))
  }
}

// -- Previews -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
  VanceTheme {
    BottomNavigationBar(
      backgroundColor = MaterialTheme.colors.background,
      windowSizeClass = WindowSizeClass.calculateFromSize(Zero)
    )
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

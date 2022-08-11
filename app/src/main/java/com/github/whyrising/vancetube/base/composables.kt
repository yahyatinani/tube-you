package com.github.whyrising.vancetube.base

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.initAppDb
import com.github.whyrising.vancetube.ui.theme.BackArrow
import com.github.whyrising.vancetube.ui.theme.SmallLabelText
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.y.core.v

@Composable
fun BasePanel(
  backgroundColor: Color = MaterialTheme.colors.background,
  content: @Composable (PaddingValues) -> Unit
) {
  val bottomBarHeight = 48.dp
  val bottomBarHeightPx =
    with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }
  val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }
  val visibleState = MutableTransitionState(true)

  val nestedScrollConnection = remember {
    object : NestedScrollConnection {
      override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
      ): Offset {

        Log.i("scroll", "${available.y}")
        val delta = available.y
        visibleState.targetState = delta > 0

        val newOffset = bottomBarOffsetHeightPx.value + delta
        bottomBarOffsetHeightPx.value =
          newOffset.coerceIn(-bottomBarHeightPx, 0f)

        return Offset.Zero
      }
    }
  }

  val scaffoldState = rememberScaffoldState()
  Scaffold(
    modifier = Modifier.nestedScroll(nestedScrollConnection),
    scaffoldState = scaffoldState,
    topBar = {
      AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
      ) {
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
      }
    },
    bottomBar = {
      BottomNavigation(
        modifier = Modifier.drawBehind {
          val strokeWidth = Stroke.DefaultMiter
          val y = size.height

          drawLine(
            color = Color.LightGray,
            strokeWidth = strokeWidth,
            start = Offset(0f, y),
            end = Offset(size.width, y)
          )
        },
        backgroundColor = backgroundColor,
        elevation = 0.dp,
      ) {
        BottomNavigationItem(
          selected = true,
          onClick = { /*TODO*/ },
          label = { SmallLabelText(text = "Home") },
          icon = {
            Icon(
              imageVector = Icons.Filled.Home,
              contentDescription = "Home panel",
            )
          }
        )
        BottomNavigationItem(
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
        BottomNavigationItem(
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

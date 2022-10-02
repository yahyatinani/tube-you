package com.github.whyrising.vancetube.ui.theme.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.y.core.v

@Composable
fun BackArrow() {
  IconButton(
    onClick = {
      dispatch(v(base.navigate_to, base.go_back))
    }
  ) {
    Icon(
      imageVector = Icons.Filled.ArrowBack,
      contentDescription = "Back"
    )
  }
}

val BOTTOM_BAR_HEIGHT = 48.dp

@Composable
fun VanceCompactBottomNavBar(navItems: @Composable (Modifier) -> Unit) {
  Row(
    horizontalArrangement = Arrangement.SpaceAround,
    modifier = Modifier
      .height(BOTTOM_BAR_HEIGHT)
      .fillMaxWidth()
      .selectableGroup()
  ) {
    navItems(Modifier.weight(1f))
  }
}

val BOTTOM_BAR_TOP_BORDER_THICKNESS = 1.dp

@Composable
fun VanceLargeBottomNavBar(
  modifier: Modifier = Modifier,
  navItems: @Composable () -> Unit
) = Layout(
  modifier = modifier.selectableGroup(),
  content = navItems,
  measurePolicy = object : MeasurePolicy {
    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
      measurables: List<IntrinsicMeasurable>,
      height: Int
    ): Int = measurables.maxOf { it.maxIntrinsicWidth(height) }

    override fun MeasureScope.measure(
      measurables: List<Measurable>,
      constraints: Constraints
    ): MeasureResult {
      val placeables = measurables.map { measurable ->
        measurable.measure(
          constraints.copy(
            minWidth = maxIntrinsicWidth(measurables, constraints.minHeight)
          )
        )
      }
      val maxWidth = constraints.maxWidth
      val navItem = placeables[0]
      val itemY = (BOTTOM_BAR_HEIGHT.roundToPx() - navItem.height) / 2 +
        BOTTOM_BAR_TOP_BORDER_THICKNESS.roundToPx()

      var nextItemX = maxWidth / 2 - (navItem.width * placeables.size) / 2

      return layout(width = maxWidth, height = BOTTOM_BAR_HEIGHT.roundToPx()) {
        placeables.forEach { placeable ->
          placeable.placeRelative(x = nextItemX, y = itemY)
          nextItemX += placeable.width
        }
      }
    }
  }
)

@Composable
fun VanceBottomNavItem(
  selected: Boolean,
  icon: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  label: @Composable (() -> Unit),
  onClick: () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val colorScheme = MaterialTheme.colorScheme
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .background(
        shape = CircleShape,
        color = when {
          isPressed -> {
            colorScheme.onSurface.copy(.08f)
          }
          else -> Color.Transparent
        }
      )
      .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val h = placeable.height
        val w = placeable.width
        val d = maxOf(h, w)

        layout(d, d) {
          placeable.placeRelative((d - w) / 2, (d - h) / 2)
        }
      }
      .selectable(
        selected = selected,
        interactionSource = interactionSource,
        indication = null,
        role = Role.Tab,
        onClick = onClick
      )
      .padding(horizontal = 12.dp)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      icon()
      label()
    }
  }
}

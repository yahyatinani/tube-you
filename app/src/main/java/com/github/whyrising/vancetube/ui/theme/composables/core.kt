package com.github.whyrising.vancetube.ui.theme.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.y.core.v
import kotlin.math.roundToInt

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

val BOTTOM_BAR_TOP_PADDING = 2.dp

@Composable
fun VanceCompactBottomNavBar(navItems: @Composable (Modifier) -> Unit) {
  Row(
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(top = BOTTOM_BAR_TOP_PADDING)
      .height(BOTTOM_BAR_HEIGHT)
      .fillMaxWidth()
  ) {
    navItems(Modifier.weight(1f))
  }
}

@Composable
fun VanceLargeBottomNavBar(
  modifier: Modifier = Modifier,
  navItems: @Composable () -> Unit
) {
  Layout(
    modifier = modifier.padding(top = BOTTOM_BAR_TOP_PADDING),
    content = navItems,
    measurePolicy = object : MeasurePolicy {
      override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
      ): MeasureResult {
        // Don't constrain child views further, measure them with given
        // constraints.
        // List of measured children

        val placeables = measurables.fastMap { measurable ->
          measurable.measure(
            constraints.copy(
              minWidth = maxIntrinsicWidth(
                measurables,
                constraints.minHeight
              )
            )
          )
        }
        val navItem = placeables[0]
        val height = BOTTOM_BAR_HEIGHT.toPx().roundToInt()
        val offset = 10.dp.roundToPx()
        val firstX = constraints.maxWidth / 2 -
          ((navItem.width + offset) * placeables.size) / 2
        var nextItemX = firstX
        val itemY = (height - navItem.height) / 2

        return layout(
          width = constraints.maxWidth,
          height = height
        ) {
          placeables.forEach { placeable ->
            placeable.placeRelative(x = nextItemX, y = itemY)
            nextItemX += placeable.width + offset
          }
        }
      }

      override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int
      ): Int {
        return measurables.maxOf { it.maxIntrinsicWidth(height) }
      }
    }
  )
}

@Composable
fun VanceBottomNavItem(
  modifier: Modifier,
  selected: Boolean,
  itemRoute: Any,
  icon: ImageVector,
  icDescId: Int,
  labelTxtId: Int
) {
  val interactionSource = remember { MutableInteractionSource() }
  val ripple = rememberRipple(
    bounded = false,
    color = LocalContentColor.current
  )
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .selectable(
        enabled = true,
        selected = selected,
        indication = ripple,
        role = Role.Tab,
        onClick = { dispatch(v(base.navigate_to, itemRoute)) },
        interactionSource = interactionSource
      )
      .padding(horizontal = 8.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = stringResource(icDescId),
      tint = MaterialTheme.colorScheme.onBackground
    )
    Text(
      text = stringResource(labelTxtId),
      style = MaterialTheme.typography.labelSmall
    )
  }
}

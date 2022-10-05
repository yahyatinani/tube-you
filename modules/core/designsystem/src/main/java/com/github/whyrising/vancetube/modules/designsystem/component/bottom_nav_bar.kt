package com.github.whyrising.vancetube.modules.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

val BOTTOM_BAR_HEIGHT = 48.dp
val BOTTOM_BAR_TOP_BORDER_THICKNESS = 1.dp

@Composable
fun VanceBottomNavBarCompact(
  modifier: Modifier = Modifier,
  content: @Composable (Modifier) -> Unit
) {
  Row(
    horizontalArrangement = Arrangement.SpaceAround,
    modifier = modifier
      .height(BOTTOM_BAR_HEIGHT)
      .fillMaxWidth()
      .selectableGroup()
  ) {
    content(Modifier.weight(1f))
  }
}

@Composable
fun VanceBottomNavBarLarge(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) = Layout(
  modifier = modifier.selectableGroup(),
  content = content,
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

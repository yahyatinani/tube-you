package com.github.whyrising.vancetube.ui.theme.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@Composable
fun VanceLargeBottomNavBar(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Layout(
    modifier = modifier,
    content = content,
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

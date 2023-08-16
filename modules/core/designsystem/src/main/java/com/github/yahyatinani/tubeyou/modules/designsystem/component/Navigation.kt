package com.github.yahyatinani.tubeyou.modules.designsystem.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun TyNavigationBarItem(
  selected: Boolean,
  icon: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  selectedIcon: @Composable () -> Unit = icon,
  label: @Composable () -> Unit,
  onPressColor: Color,
  onClick: () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val transition = updateTransition(
    targetState = isPressed,
    label = "Bottom navItem onPress color transition"
  )
  val backgroundColor by transition.animateColor(
    transitionSpec = {
      if (true isTransitioningTo false) {
        tween(
          durationMillis = 100,
          easing = FastOutLinearInEasing
        )
      } else {
        tween(
          durationMillis = 10,
          delayMillis = 50,
          easing = LinearEasing
        )
      }
    },
    label = "Bottom navItem onPress color animation"
  ) { state ->
    if (state) onPressColor else Color.Transparent
  }
  val borderColor by transition.animateColor(
    transitionSpec = {
      if (true isTransitioningTo false) {
        tween(
          durationMillis = 700,
          easing = LinearEasing
        )
      } else {
        tween(
          durationMillis = 10,
          delayMillis = 50,
          easing = LinearEasing
        )
      }
    },
    label = "Bottom navItem onPress color2 animation"
  ) { state ->
    if (state) onPressColor else Color.Transparent
  }
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .background(
        shape = CircleShape,
        color = backgroundColor
      )
      .border(
        width = 1.dp,
        color = if (isPressed) Color.Transparent else borderColor,
        shape = CircleShape
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
      if (selected) selectedIcon() else icon()
      label()
    }
  }
}

val BOTTOM_BAR_HEIGHT = 48.dp
val BOTTOM_BAR_TOP_BORDER_THICKNESS = 1.dp

@Composable
private fun TyNavigationBarCompact(
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
private fun TyNavigationBarLarge(
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

@Composable
fun TyNavigationBar(
  modifier: Modifier = Modifier,
  isCompact: Boolean,
  borderColor: Color,
  content: @Composable (Modifier) -> Unit
) {
  Surface(
    modifier = modifier.windowInsetsPadding(NavigationBarDefaults.windowInsets)
  ) {
    Box(contentAlignment = Alignment.TopCenter) {
      Divider(
        modifier = Modifier.fillMaxWidth(),
        thickness = BOTTOM_BAR_TOP_BORDER_THICKNESS,
        color = borderColor
      )
      if (isCompact) {
        TyNavigationBarCompact(content = content)
      } else {
        TyNavigationBarLarge(content = { content(Modifier) })
      }
    }
  }
}

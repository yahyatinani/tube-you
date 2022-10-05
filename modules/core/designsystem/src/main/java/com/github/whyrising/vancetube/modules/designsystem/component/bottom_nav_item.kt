package com.github.whyrising.vancetube.modules.designsystem.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun VanceBottomNavItem(
  selected: Boolean,
  icon: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  label: @Composable (() -> Unit),
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
      icon()
      label()
    }
  }
}

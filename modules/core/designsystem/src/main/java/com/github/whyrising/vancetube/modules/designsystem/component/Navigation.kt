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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.y.core.get

@Composable
fun TyNavigationItem(
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

val BOTTOM_BAR_HEIGHT = 48.dp
val BOTTOM_BAR_TOP_BORDER_THICKNESS = 1.dp

@Composable
fun TyNavigationBarCompact(
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
fun TyNavigationBarLarge(
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
fun TyBottomNavigationBar(
  navItems: Map<Any, Any>,
  isCompact: Boolean,
  colorScheme: ColorScheme,
  onNavItemPress: (route: Any) -> Unit
) {
  Surface(
    modifier = Modifier.windowInsetsPadding(NavigationBarDefaults.windowInsets)
  ) {
    val content: @Composable (Modifier) -> Unit = {
      navItems.forEach { (route, navItem) ->
        val contentDescription = stringResource(
          get(navItem, common.icon_content_desc_text_id)!!
        )
        val text = stringResource(get(navItem, common.label_text_id)!!)
        val selected: Boolean = get(navItem, common.is_selected)!!
        val lightGray = colorScheme.onSurface.copy(.12f)
        TyNavigationItem(
          selected = selected,
          icon = {
            val id = get<Any>(navItem, common.icon)!!

            if (id is Int) {
              Icon(
                painter = painterResource(id),
                contentDescription = contentDescription,
                tint = colorScheme.onBackground,
                modifier = Modifier.then(
                  if (selected) Modifier.size(32.dp) else Modifier
                )
              )
            } else if (id is ImageVector) {
              Icon(
                imageVector = id,
                contentDescription = contentDescription,
                tint = colorScheme.onBackground,
                modifier = Modifier.then(
                  if (selected) Modifier.size(32.dp) else Modifier
                )
              )
            }
          },
          label = {
            val t = MaterialTheme.typography
            Text(
              text = text,
              style = if (selected) t.labelMedium else t.labelSmall
            )
          },
          onPressColor = lightGray,
          onClick = { onNavItemPress(route) }
        )
      }
    }

    Box(contentAlignment = Alignment.TopCenter) {
      val lightGray = colorScheme.onSurface.copy(.12f)
      Divider(
        modifier = Modifier.fillMaxWidth(),
        thickness = BOTTOM_BAR_TOP_BORDER_THICKNESS,
        color = lightGray
      )
      if (isCompact) {
        TyNavigationBarCompact(content = content)
      } else {
        TyNavigationBarLarge { content(Modifier) }
      }
    }
  }
}

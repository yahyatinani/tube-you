package com.github.whyrising.vancetube.base

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize.Companion.Zero
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.fx.FxIds
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.recompose.regFx
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.base.base.bottom_nav_items
import com.github.whyrising.vancetube.base.base.expand_top_app_bar
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.vancetube.home.homeLarge
import com.github.whyrising.vancetube.library.library
import com.github.whyrising.vancetube.subscriptions.subscriptions
import com.github.whyrising.vancetube.trends.trending
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.vancetube.ui.theme.composables.BackArrow
import com.github.whyrising.vancetube.ui.theme.isCompact
import com.github.whyrising.y.core.getFrom
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlin.math.roundToInt

const val ANIM_OFFSET_X = 300

@Composable
private fun DestinationTrackingSideEffect(navController: NavHostController) {
  DisposableEffect(navController) {
    val listener = NavController
      .OnDestinationChangedListener { _, navDestination, _ ->
        navDestination.route.let {
          if (it != null) {
            dispatch(v(base.on_bottom_nav_click, it))
          }
        }
        // FIXME: use this when a new activity on top
//        val previousBackStackEntry = navCtrl.previousBackStackEntry
//        if (previousBackStackEntry != null) {
//          val route = previousBackStackEntry.destination.route
//          if (route != null) dispatch(v(base.current_bottom_nav_panel, route))
//        }
//        dispatch(v(base.set_backstack_status, flag))
      }

    navController.addOnDestinationChangedListener(listener)

    onDispose {
      navController.removeOnDestinationChangedListener(listener)
    }
  }
}

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalComposeUiApi::class,
  ExperimentalAnimationApi::class,
  ExperimentalLayoutApi::class
)
@Composable
fun VanceApp(
  windowSizeClass: WindowSizeClass,
  navController: NavHostController = rememberAnimatedNavController()
) {
  DestinationTrackingSideEffect(navController)

  LaunchedEffect(Unit) {
    regBaseFx(navController)
    regBaseEventHandlers
  }
  regBaseSubs

  val isCompactDisplay = isCompact(windowSizeClass)
  VanceTheme(isCompact = isCompactDisplay) {
    val scrollBehavior = when {
      isCompactDisplay -> {
        val topAppBarState = rememberTopAppBarState()
        LaunchedEffect(Unit) {
          regFx(expand_top_app_bar) {
            topAppBarState.heightOffset = 0f
          }
          regEventFx(expand_top_app_bar) { _, _ ->
            m(FxIds.fx to v(v(expand_top_app_bar)))
          }
        }
        enterAlwaysScrollBehavior(topAppBarState)
      }
      else -> pinnedScrollBehavior()
    }
    Scaffold(
      modifier = Modifier
        .then(
          if (isCompactDisplay) {
            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
          } else Modifier
        )
        .semantics {
          // Allows to use testTag() for UiAutomator resource-id.
          testTagsAsResourceId = true
        },
      topBar = {
        TopAppBar(
          modifier = Modifier.windowInsetsPadding(
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top + Horizontal)
          ),
          title = {
            IconButton(onClick = { /*TODO*/ }) {
              Icon(
                imageVector = Icons.Outlined.Search,
                modifier = Modifier.size(26.dp),
                contentDescription = "Search a video"
              )
            }
          },
          scrollBehavior = scrollBehavior,
          navigationIcon = {
            if (subscribe<Boolean>(v(base.is_backstack_available)).w()) {
              BackArrow()
            }
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
//        val color = MaterialTheme.colorScheme.onSurface.copy(.15f)
        val color = Color(0xFF3A3A3A)
        Surface(
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier
            .drawWithCache {
              onDrawWithContent {
                drawContent()
                val strokeWidth = 2.dp.value
                drawLine(
                  color = color,
                  start = Offset(0f, strokeWidth / 2),
                  end = Offset(size.width, strokeWidth / 2),
                  strokeWidth = strokeWidth
                )
              }
            }
            .windowInsetsPadding(
              WindowInsets.safeDrawing.only(Horizontal + Bottom)
            ),
        ) {
          Layout(
            modifier = Modifier,
            content = {
              subscribe<Map<Any, Any>>(v(bottom_nav_items)).w()
                .forEach { (route, navItem) ->
                  val interactionSource = remember {
                    MutableInteractionSource()
                  }
                  val ripple = rememberRipple(
                    bounded = false,
                    color = LocalContentColor.current
                  )
                  Column(
                    horizontalAlignment = CenterHorizontally,
                    modifier = Modifier
                      .selectable(
                        enabled = true,
                        selected = getFrom(navItem, base.is_selected)!!,
                        indication = ripple,
                        role = Role.Tab,
                        onClick = { dispatch(v(base.navigate_to, route)) },
                        interactionSource = interactionSource,
                      )
                      .padding(horizontal = 8.dp)
                  ) {
                    Icon(
                      imageVector = getFrom(navItem, base.icon)!!,
                      contentDescription = stringResource(
                        getFrom(navItem, base.icon_content_desc_text_id)!!
                      ),
                      tint = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                      text = stringResource(
                        getFrom(navItem, base.label_text_id)!!
                      ),
                      style = MaterialTheme.typography.labelSmall
                    )
                  }
                }
            },
            measurePolicy = object : MeasurePolicy {
              override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints
              ): MeasureResult {
                // Don't constrain child views further, measure them with given
                // constraints.
                // List of measured children

                val placeables = measurables.map { measurable ->
                  measurable.measure(
                    constraints.copy(
                      minWidth = maxIntrinsicWidth(
                        measurables,
                        constraints.minHeight
                      )
                    )
                  )
                }
                val placeable1 = placeables[0]
                val BottomAppBarHeight = 48.dp
                val height = BottomAppBarHeight.toPx().roundToInt()
                val offset = 10.dp.roundToPx()
                val itemY = (height - placeable1.height) / 2
                return layout(
                  width = constraints.maxWidth,
                  height = height,
                  alignmentLines = emptyMap(),
                ) {
                  var itemX =
                    constraints.maxWidth / 2 - ((placeable1.width + offset) * placeables.size) / 2
                  placeables.forEach { placeable ->
                    placeable.placeRelative(x = itemX, y = itemY)
                    itemX += placeable.width + offset
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

          //            Row(
//              horizontalArrangement = Arrangement.Center,
//              modifier = Modifier
//                .height(48.dp)
//                .padding(top = 4.dp)
//                .fillMaxWidth()
//                .then(
//                  object : LayoutModifier {
//                    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
//                      measurable: IntrinsicMeasurable,
//                      height: Int
//                    ): Int {
//                      return measurable.maxIntrinsicWidth(height)
//                    }
//                    override fun MeasureScope.measure(
//                      measurable: Measurable,
//                      constraints: Constraints
//                    ): MeasureResult {
//                      // Don't constrain child views further, measure them with given
//                      // constraints.
//                      // List of measured children
//
//                      val placeable = measurable.measure(
//                        constraints.copy(
//                          minWidth = maxIntrinsicWidth(
//                            measurable,
//                            constraints.minHeight
//                          )
//                        )
//                      )
//
//                      // Set the size of the layout as big as it can
//                      return layout(
//                        width = placeable.width,
//                        height = placeable.height,
//                      ) {
//                        // Track the y co-ord we have placed children up to
//
//                        placeable.placeRelative(x = 0, y = 0)
//                      }
//                    }
//                  }
//                )
//            ) {
//              subscribe<Map<Any, Any>>(v(bottom_nav_items)).w()
//                .forEach { (route, navItem) ->
//                  val interactionSource = remember {
//                    MutableInteractionSource()
//                  }
//                  val ripple = rememberRipple(
//                    bounded = false,
//                    color = LocalContentColor.current
//                  )
//                  Column(
//                    horizontalAlignment = CenterHorizontally,
//                    modifier = Modifier
//                      .padding(horizontal = 8.dp)
//                      .selectable(
//                        enabled = true,
//                        selected = getFrom(navItem, base.is_selected)!!,
//                        indication = ripple,
//                        role = Role.Tab,
//                        onClick = { dispatch(v(base.navigate_to, route)) },
//                        interactionSource = interactionSource,
//                      )
//                  ) {
//                    Icon(
//                      imageVector = getFrom(navItem, base.icon)!!,
//                      contentDescription = stringResource(
//                        getFrom(navItem, base.icon_content_desc_text_id)!!
//                      ),
//                      tint = MaterialTheme.colorScheme.onBackground
//                    )
//                    Text(
//                      text = stringResource(
//                        getFrom(navItem, base.label_text_id)!!
//                      ),
//                      style = MaterialTheme.typography.labelSmall
//                    )
//                  }
//                }
//            }

        }
      }
    ) {
      val orientation = LocalConfiguration.current.orientation
      AnimatedNavHost(
        navController = navController,
        startDestination = subscribe<String>(v(base.start_route)).w(),
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
          .padding(it)
          .consumedWindowInsets(it)
      ) {
        if (isCompactDisplay) {
          home(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
          trending(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
          subscriptions(animOffsetX = ANIM_OFFSET_X, orientation = orientation)
          library(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
        } else {
          homeLarge(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
          trending(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
          subscriptions(animOffsetX = ANIM_OFFSET_X, orientation = orientation)
          library(animOffSetX = ANIM_OFFSET_X, orientation = orientation)
        }
      }
    }
  }
}

// -- Previews -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun BasePanelPreview() {
  initAppDb()
  VanceApp(windowSizeClass = WindowSizeClass.calculateFromSize(Zero))
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BasePanelDarkPreview() {
  BasePanelPreview()
}

package com.github.whyrising.vancetube

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize.Companion.Zero
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.dispatchSync
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.recompose.regFx
import com.github.whyrising.recompose.watch
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.active_navigation_item
import com.github.whyrising.vancetube.modules.core.keywords.common.expand_top_app_bar
import com.github.whyrising.vancetube.modules.core.keywords.common.icon
import com.github.whyrising.vancetube.modules.core.keywords.common.is_selected
import com.github.whyrising.vancetube.modules.core.keywords.common.label_text_id
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.designsystem.component.BOTTOM_BAR_TOP_BORDER_THICKNESS
import com.github.whyrising.vancetube.modules.designsystem.component.SearchSuggestionItem
import com.github.whyrising.vancetube.modules.designsystem.component.VanceNavigationBarCompact
import com.github.whyrising.vancetube.modules.designsystem.component.VanceNavigationBarLarge
import com.github.whyrising.vancetube.modules.designsystem.component.VanceNavigationItem
import com.github.whyrising.vancetube.modules.designsystem.theme.VanceTheme
import com.github.whyrising.vancetube.modules.designsystem.theme.isCompact
import com.github.whyrising.vancetube.modules.panel.home.getRegHomeEvents
import com.github.whyrising.vancetube.modules.panel.home.home
import com.github.whyrising.vancetube.modules.panel.home.homeLarge
import com.github.whyrising.vancetube.modules.panel.home.regHomeCofx
import com.github.whyrising.vancetube.modules.panel.home.regHomeSubs
import com.github.whyrising.vancetube.modules.panel.library.library
import com.github.whyrising.vancetube.modules.panel.subscriptions.subscriptions
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import kotlinx.coroutines.CoroutineScope

private val navChangedListener: (
  controller: NavController,
  destination: NavDestination,
  arguments: Bundle?
) -> Unit = { navCtrl, destination, _ ->
  navCtrl.apply {
    destination.route?.let { dispatch(v(active_navigation_item, it)) }
  }
  // dispatch(v(base.set_backstack_status, flag))
}

@Composable
private fun NavigationChangedListenerEffect(navController: NavHostController) {
  DisposableEffect(navController) {
    navController.addOnDestinationChangedListener(navChangedListener)

    onDispose {
      navController.removeOnDestinationChangedListener(navChangedListener)
    }
  }
}

@Composable
private fun ProfileIcon(colorScheme: ColorScheme) {
  Box(
    modifier = Modifier
      .background(
        shape = CircleShape,
        color = Color.Transparent
      )
      .clickable(role = Role.Image) { /*TODO*/ }
      .padding(8.dp)
  ) {
    Box(
      modifier = Modifier
        .background(
          shape = CircleShape,
//                    color = Color(0xFFEB3F7A),
          color = colorScheme.onBackground
        )
        .width(24.dp)
        .height(24.dp)
        .padding(3.dp),
      contentAlignment = Center
    ) {
      Icon(
        imageVector = Icons.Filled.Person,
        modifier = Modifier,
//                    .clip(CircleShape)
//                    .size(20.dp),
        contentDescription = "profile picture",
        tint = colorScheme.background
      )
    }
  }
}

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalComposeUiApi::class,
  ExperimentalLayoutApi::class
)
@Composable
fun VanceApp(
  windowSizeClass: WindowSizeClass,
  navController: NavHostController = rememberNavController()
) {
  NavigationChangedListenerEffect(navController)

  val scope: CoroutineScope = rememberCoroutineScope()
  LaunchedEffect(Unit) {
    regCommonFx(navController)

    regCofx(home.coroutine_scope) { cofx ->
      cofx.assoc(home.coroutine_scope, scope)
    }
    getRegHomeEvents()
    dispatch(v(home.initialize))
  }
  regHomeSubs

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
            m(fx to v(v(expand_top_app_bar)))
          }
        }
        enterAlwaysScrollBehavior(topAppBarState)
      }

      else -> pinnedScrollBehavior()
    }
    val colorScheme = MaterialTheme.colorScheme
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
        val searchBarState = watch<SearchBarState?>(query = v(":search_bar"))
        if (searchBarState != null) {
          val focusRequester = FocusRequester()
          val placeHolderColor = colorScheme.onSurface.copy(alpha = .6f)

          SearchBar(
            modifier = Modifier
              .focusRequester(focusRequester),
            query = searchBarState.query,
            active = searchBarState.isActive,
            tonalElevation = 0.dp,
            shape = RoundedCornerShape(30.dp),
            colors = SearchBarDefaults.colors(
              inputFieldColors = SearchBarDefaults.inputFieldColors(
                focusedPlaceholderColor = placeHolderColor,
                unfocusedPlaceholderColor = placeHolderColor
              )
            ),
            placeholder = { Text(text = "Search YouTube") },
            leadingIcon = {
              IconButton(
                onClick = {
                  // TODO:
                  dispatch(v(":hide_search_bar"))
                }
              ) {
                Icon(
                  imageVector = Icons.Filled.ArrowBack,
                  modifier = Modifier,
                  contentDescription = ""
                )
              }
            },
            trailingIcon = {
              IconButton(onClick = { }) {
                Icon(
                  imageVector = Icons.Filled.Close,
                  modifier = Modifier,
                  contentDescription = ""
                )
              }
            },
            onQueryChange = {
              dispatchSync(v(":query", it))
            },
            onActiveChange = {
              dispatch(v(common.is_search_bar_active, it))
            },
            onSearch = {
              dispatch(v(common.navigate_to, "search_query"))
              dispatch(v(common.is_search_bar_active, false))
            }
          ) {
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
              items(
                key = { it.hashCode() },
                items = searchBarState.suggestions.value
              ) {
                SearchSuggestionItem(text = it, onClick = { /* todo: */ })
              }
            }
          }

          LaunchedEffect(Unit) {
            focusRequester.requestFocus()
          }

          BackHandler {
            dispatch(v(":hide_search_bar"))
          }
        } else {
          TopAppBar(
            modifier = Modifier
              .windowInsetsPadding(
                insets = WindowInsets.safeDrawing.only(Top + Horizontal)
              )
              .padding(end = if (isCompactDisplay) 4.dp else 16.dp),
            title = {},
            scrollBehavior = scrollBehavior,
            navigationIcon = {},
            actions = {
              IconButton(
                onClick = {
                  dispatch(v(":show_search_bar"))
                }
              ) {
                Icon(
                  imageVector = Icons.Outlined.Search,
                  modifier = Modifier.size(26.dp),
                  contentDescription = "Search a video"
                )
              }
              ProfileIcon(colorScheme)
            },
            colors = topAppBarColors(
              scrolledContainerColor = colorScheme.background
            )
          )
        }
      },
      bottomBar = {
        Surface(
          modifier = Modifier.windowInsetsPadding(
            NavigationBarDefaults.windowInsets
          )
        ) {
          Box(contentAlignment = TopCenter) {
            val lightGray = colorScheme.onSurface.copy(.12f)
            Divider(
              modifier = Modifier.fillMaxWidth(),
              thickness = BOTTOM_BAR_TOP_BORDER_THICKNESS,
              color = lightGray
            )
            val content: @Composable (Modifier) -> Unit = { modifier ->
              watch<Map<Any, Any>>(v(common.navigation_items))
                .forEach { (route, navItem) ->
                  val contentDescription = stringResource(
                    get(navItem, common.icon_content_desc_text_id)!!
                  )
                  val text = stringResource(get(navItem, label_text_id)!!)
                  val selected: Boolean = get(navItem, is_selected)!!
                  VanceNavigationItem(
                    selected = selected,
                    modifier = modifier,
                    icon = {
                      val id = get<Any>(navItem, icon)!!

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
                    onPressColor = lightGray
                  ) {
                    dispatch(v(common.on_click_nav_item, route))
                  }
                }
            }

            if (isCompact(windowSizeClass = windowSizeClass)) {
              VanceNavigationBarCompact(content = content)
            } else {
              VanceNavigationBarLarge { content(Modifier) }
            }
          }
        }
      }
    ) {
      val orientation = LocalConfiguration.current.orientation
      NavHost(
        navController = navController,
        startDestination = home.route.toString(),
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.safeDrawing.only(Horizontal))
          .padding(it)
          .consumeWindowInsets(it)
      ) {
        composable("search_query") {
          Text(text = "Search results...")
        }
        if (isCompactDisplay) {
          home(orientation = orientation)
          subscriptions(orientation = orientation)
          library(orientation = orientation)
        } else {
          homeLarge(orientation = orientation)
          subscriptions(orientation = orientation)
          library(orientation = orientation)
        }
      }
    }
  }
}

// -- Previews -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun AppPreview() {
  regCommonCofx(LocalContext.current)
  regHomeCofx
  regCommonEvents
  regCommonSubs
  dispatchSync(v(common.initialize))

  VanceApp(windowSizeClass = WindowSizeClass.calculateFromSize(Zero))
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppDarkPreview() {
  AppPreview()
}

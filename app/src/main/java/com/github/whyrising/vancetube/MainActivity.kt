package com.github.whyrising.vancetube

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.vancetube.about.about
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.vancetube.base.regBaseEventHandlers
import com.github.whyrising.vancetube.base.regBaseFx
import com.github.whyrising.vancetube.base.regBaseSubs
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.vancetube.home.regHomeEvents
import com.github.whyrising.vancetube.home.regHomeSubs
import com.github.whyrising.vancetube.ui.theme.BackArrow
import com.github.whyrising.vancetube.ui.theme.VanceTheme
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun Main(content: @Composable (PaddingValues) -> Unit) {
  VanceTheme {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    val bgColor = MaterialTheme.colors.background
    SideEffect {
      systemUiController.setSystemBarsColor(
        color = bgColor,
        darkIcons = useDarkIcons
      )
    }

    Scaffold(
      topBar = {
        TopAppBar(
          elevation = 0.dp,
          backgroundColor = MaterialTheme.colors.background,
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
    ) { paddingValues ->
      content(paddingValues)
    }
  }
}

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalAnimationApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen().apply {
      // TODO: remove this.
//       setKeepOnScreenCondition { true }
    }
    super.onCreate(savedInstanceState)

    regBaseEventHandlers()
    regBaseSubs()
    regHomeEvents()
    regHomeSubs()

    setContent {
      val navController = rememberAnimatedNavController().apply {
        addOnDestinationChangedListener { controller, _, _ ->
          val flag = controller.previousBackStackEntry != null
          dispatch(v(base.set_backstack_status, flag))
        }
      }
      regBaseFx(navController)

      Main {
        AnimatedNavHost(
          modifier = androidx.compose.ui.Modifier.padding(it),
          navController = navController,
          startDestination = home.panel.name
        ) {
          home(animOffSetX = 300)
          about(animOffSetX = 300)
        }
      }
    }
  }
}

// -- Previews -----------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun MainPreview() {
  initAppDb()
  regBaseEventHandlers()
  regBaseSubs()
  Main {}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainDarkPreview() {
  Main {}
}

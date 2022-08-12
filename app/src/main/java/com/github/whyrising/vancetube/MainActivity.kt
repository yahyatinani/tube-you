package com.github.whyrising.vancetube

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.vancetube.about.about
import com.github.whyrising.vancetube.base.BasePanel
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.vancetube.base.regBaseEventHandlers
import com.github.whyrising.vancetube.base.regBaseFx
import com.github.whyrising.vancetube.base.regBaseSubs
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.vancetube.home.regHomeEvents
import com.github.whyrising.vancetube.home.regHomeFx
import com.github.whyrising.vancetube.home.regHomeSubs
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
    val backgroundColor = MaterialTheme.colors.background
    SideEffect {
      systemUiController.setSystemBarsColor(
        color = backgroundColor,
        darkIcons = useDarkIcons
      )
    }

    BasePanel(backgroundColor, content)
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
    regHomeFx(lifecycleScope)
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

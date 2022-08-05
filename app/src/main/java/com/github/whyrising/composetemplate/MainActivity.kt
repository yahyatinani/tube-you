package com.github.whyrising.composetemplate

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.whyrising.composetemplate.about.about
import com.github.whyrising.composetemplate.base.base
import com.github.whyrising.composetemplate.base.regBaseEventHandlers
import com.github.whyrising.composetemplate.base.regBaseSubs
import com.github.whyrising.composetemplate.home.home
import com.github.whyrising.composetemplate.home.regHomeEvents
import com.github.whyrising.composetemplate.home.regHomeSubs
import com.github.whyrising.composetemplate.ui.theme.BackArrow
import com.github.whyrising.composetemplate.ui.theme.TemplateTheme
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.regFx
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Main() {
  val navController = rememberAnimatedNavController().apply {
    addOnDestinationChangedListener { controller, _, _ ->
      val flag = controller.previousBackStackEntry != null
      dispatch(v(base.set_backstack_status, flag))
    }
  }

  regFx(base.navigate) {
    when (val route = "$it") {
      base.go_back.name -> navController.popBackStack()
      else -> navController.navigate(route)
    }
  }

  TemplateTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(text = "Home")
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
      AnimatedNavHost(
        modifier = Modifier.padding(paddingValues),
        navController = navController,
        startDestination = home.home_panel.name
      ) {
        home(animOffSetX = 300)
        about(animOffSetX = 300)
      }
    }
  }
}

class MainActivity : ComponentActivity() {
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
      Main()
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
  Main()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainDarkPreview() {
  Main()
}

package com.github.whyrising.vancetube

import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.github.whyrising.recompose.cofx.Coeffects
import com.github.whyrising.recompose.cofx.regCofx
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
fun Main(
  setNotchAreaColorInLandscape: (Color) -> Unit = {},
  windowSizeClass: WindowSizeClass,
  content: @Composable (PaddingValues) -> Unit
) {
  VanceTheme(windowSizeClass = windowSizeClass) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    val backgroundColor = MaterialTheme.colors.background

    SideEffect {
      setNotchAreaColorInLandscape(backgroundColor)

      systemUiController.setSystemBarsColor(
        color = backgroundColor,
        darkIcons = useDarkIcons
      )
    }

    BasePanel(backgroundColor, windowSizeClass, content)
  }
}

class MainActivity : ComponentActivity() {
  private fun screenWidthPx() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
      val metrics = windowManager.currentWindowMetrics
      val insets =
        metrics.windowInsets.getInsets(
          android.view.WindowInsets.Type.systemBars()
        )
      metrics.bounds.width() - insets.left - insets.right
    }
    else -> {
      val view = window.decorView
      val insets =
        WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
          .getInsets(WindowInsetsCompat.Type.systemBars())
      resources.displayMetrics.widthPixels - insets.left - insets.right
    }
  }

  private fun pxToDp(inPx: Int) =
    (inPx / resources.displayMetrics.density).toInt()

  @OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
  )
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
    regHomeSubs(this)

    regCofx(home.thumbnail_height) { coeffects: Coeffects ->
      val nativeThumbnailHeight = 720
      val relativeHeightInPx = (screenWidthPx() * nativeThumbnailHeight) / 1280
      val heightInPx = when {
        relativeHeightInPx > nativeThumbnailHeight -> nativeThumbnailHeight
        else -> relativeHeightInPx
      }
      coeffects.assoc(
        home.thumbnail_height,
        pxToDp(heightInPx)
      )
    }

    setContent {
      val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(this)
      Log.i("windowSizeClass", "$windowSizeClass")

      val navController = rememberAnimatedNavController().apply {
        addOnDestinationChangedListener { controller, _, _ ->
          val flag = controller.previousBackStackEntry != null
          dispatch(v(base.set_backstack_status, flag))
        }
      }
      regBaseFx(navController)

      val configuration = LocalConfiguration.current
      Main(
        setNotchAreaColorInLandscape = {
          if (configuration.orientation == ORIENTATION_LANDSCAPE) {
            val bitmap = createBitmap(24, 24, Bitmap.Config.ARGB_8888).apply {
              eraseColor(it.toArgb())
            }
            window.setBackgroundDrawable(BitmapDrawable(resources, bitmap))
          }
        },
        windowSizeClass
      ) {
        AnimatedNavHost(
          navController = navController,
          startDestination = home.panel.name
        ) {
          home(animOffSetX = 300, it)
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
//  Main(window = ) {}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainDarkPreview() {
//  Main {}
}

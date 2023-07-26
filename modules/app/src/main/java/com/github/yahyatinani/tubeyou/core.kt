package com.github.yahyatinani.tubeyou

import android.app.Application
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.OrientationEventListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.panel.common.regCommonEvents
import com.github.yahyatinani.tubeyou.modules.panel.common.regCommonSubs
import com.github.yahyatinani.tubeyou.modules.panel.common.search.regSearchEvents
import com.github.yahyatinani.tubeyou.modules.panel.common.search.regSearchSubs
import com.github.yahyatinani.tubeyou.modules.panel.common.tyHttpClient
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.TyPlayer
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.httpfx.httpFxClient
import io.github.yahyatinani.recompose.httpfx.regBounceFx
import io.github.yahyatinani.recompose.httpfx.regHttpKtor
import io.github.yahyatinani.recompose.pagingfx.regPagingFx
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.recompose.regFx
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

// -- Application Implementation -----------------------------------------------

class TyApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    TyPlayer.initInstance(applicationContext)

    httpFxClient = tyHttpClient

    regBounceFx
    regHttpKtor
    regPagingFx()

    regAppCofx(this)
    regAppEvents()
    regAppSubs()
    regSearchEvents()
    regCommonEvents()
    regSearchSubs()
    regCommonSubs()

    dispatchSync(v(common.initialize))

    // FIXME: remove this for release
//    System.setProperty("kotlinx.coroutines.debug", "on")
//    Log.i("currentThreadName", Thread.currentThread().name)
  }
}

// -- Entry Point --------------------------------------------------------------

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen().apply {
      // FIXME: remove me! setKeepOnScreenCondition { true }
    }
    super.onCreate(savedInstanceState)

    val orientationEventListener: OrientationEventListener =
      object : OrientationEventListener(this) {
        override fun onOrientationChanged(orientation: Int) {
          val epsilon = 10
          val leftLandscape = 90
          val rightLandscape = 270
          if (epsilonCheck(orientation, leftLandscape, epsilon) ||
            epsilonCheck(orientation, rightLandscape, epsilon)
          ) {
            this@MainActivity.requestedOrientation = SCREEN_ORIENTATION_SENSOR
          }
        }

        private fun epsilonCheck(a: Int, b: Int, epsilon: Int): Boolean {
          return a > b - epsilon && a < b + epsilon
        }
      }
    orientationEventListener.enable()

    val bitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888).apply {
      eraseColor(ContextCompat.getColor(this@MainActivity, R.color.black_900))
    }
    window.setBackgroundDrawable(BitmapDrawable(resources, bitmap))

    WindowCompat.setDecorFitsSystemWindows(window, true)

    regFx(":player_fullscreen_landscape") {
      runOnUiThread {
        val bm =
          Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888).apply {
            eraseColor(ContextCompat.getColor(this@MainActivity, R.color.black))
          }
        window.setBackgroundDrawable(BitmapDrawable(resources, bm))

        with(WindowCompat.getInsetsController(window, window.decorView)) {
          systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
          hide(WindowInsetsCompat.Type.systemBars())
        }
      }
    }

    regFx(":player_portrait") {
      runOnUiThread {
        with(WindowCompat.getInsetsController(window, window.decorView)) {
          systemBarsBehavior = BEHAVIOR_DEFAULT
          show(WindowInsetsCompat.Type.systemBars())
        }
      }
    }

    regFx(":toggle_orientation") {
      when (resources.configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
          requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
        }

        else -> {
          requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
          requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
        }
      }
    }

    regEventFx(":player_fullscreen_landscape") { _, _ ->
      m(BuiltInFx.fx to v(v(":player_fullscreen_landscape")))
    }

    regEventFx(":player_portrait") { _, _ ->
      m(BuiltInFx.fx to v(v(":player_portrait")))
    }

    regEventFx(":toggle_orientation") { _, _ ->
      m(BuiltInFx.fx to v(v(":toggle_orientation")))
    }

    setContent {
      TyApp(windowSizeClass = calculateWindowSizeClass(this))
    }
  }

  override fun onResume() {
    super.onResume()

    dispatch(v(common.expand_top_app_bar))
  }
}

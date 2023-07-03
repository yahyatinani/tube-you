package com.github.yahyatinani.tubeyou

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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

    WindowCompat.setDecorFitsSystemWindows(window, true)

    regFx(":player_fullscreen_landscape") {
      runOnUiThread {
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

    regEventFx(":player_fullscreen_landscape") { _, _ ->
      m(BuiltInFx.fx to v(v(":player_fullscreen_landscape")))
    }

    regEventFx(":player_portrait") { _, _ ->
      m(BuiltInFx.fx to v(v(":player_portrait")))
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

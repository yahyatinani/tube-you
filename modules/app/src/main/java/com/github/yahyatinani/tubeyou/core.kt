package com.github.yahyatinani.tubeyou

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.dispatchSync
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.panel.common.regBounceFx
import com.github.yahyatinani.tubeyou.modules.panel.common.regHttpKtor
import com.github.yahyatinani.tubeyou.modules.panel.common.search.regCommonEvents
import com.github.yahyatinani.tubeyou.modules.panel.common.search.regCommonSubs

// -- Application Implementation -----------------------------------------------

class TyApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    regHttpKtor()
    regBounceFx()

    regAppCofx(this)
    regAppEvents()
    regAppSubs()
    regCommonEvents()
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

    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      TyApp(windowSizeClass = calculateWindowSizeClass(this))
    }
  }

  override fun onResume() {
    super.onResume()

    dispatch(v(common.expand_top_app_bar))
  }
}

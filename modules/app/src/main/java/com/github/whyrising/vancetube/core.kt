package com.github.whyrising.vancetube

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
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.panel.common.regBounceFx
import com.github.whyrising.vancetube.modules.panel.common.regHttpKtor
import com.github.whyrising.vancetube.modules.panel.home.regHomeCofx
import com.github.whyrising.y.core.v

// -- Application Implementation -----------------------------------------------

class VanceApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    regCommonCofx(this)
    regHomeCofx
    regCommonEvents
    regCommonSubs

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

    regHttpKtor()
    regBounceFx()

    setContent {
      VanceApp(calculateWindowSizeClass(this))
    }
  }

  override fun onResume() {
    super.onResume()

    dispatch(v(common.expand_top_app_bar))
  }
}

package io.github.yahyatinani.tubeyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.isCompact
import io.github.yahyatinani.tubeyou.events.regTyEvents
import io.github.yahyatinani.tubeyou.subs.RegTySubs
import io.github.yahyatinani.tubeyou.ui.TyApp

/*
 * Profiling:
 *
 * Java: ~ 9.4 MB
 * Total: ~ 88.2 MB
 */

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)

    // Turn off the decor fitting system windows, which allows us to handle
    // insets, including IME animations, and go edge-to-edge.
    // This also sets up the initial system bar style based on the platform
    // theme.
    enableEdgeToEdge()

    regTyEvents() // todo: clear these events when done.

    setContent {
      RegTySubs()
      TyTheme(isCompact = isCompact(calculateWindowSizeClass(this))) {
        TyApp()
      }
    }
  }

  override fun onStop() {
    super.onStop()

//    finish()
  }
}

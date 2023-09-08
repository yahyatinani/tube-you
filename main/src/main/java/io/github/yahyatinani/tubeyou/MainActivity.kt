package io.github.yahyatinani.tubeyou

import android.content.ComponentName
import android.os.Bundle
import android.view.OrientationEventListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.isCompact
import com.google.common.util.concurrent.MoreExecutors
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.tubeyou.events.regTyEvents
import io.github.yahyatinani.tubeyou.subs.RegTySubs
import io.github.yahyatinani.tubeyou.ui.TyApp
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fx.PlaybackService
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fx.RegWatchFx
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fx.TyOrientationEventListener
import io.github.yahyatinani.y.core.v

/*
 * Profiling:
 *
 * Java: ~ 9.4 MB
 * Total: ~ 88.2 MB
 */

class MainActivity : ComponentActivity() {
  private var orientationEventListener: OrientationEventListener? = null

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
      val orientation = LocalConfiguration.current.orientation
      RegWatchFx(this, orientation)
      RegTySubs()
      val windowSizeClass = calculateWindowSizeClass(this)
      TyTheme(isCompact = isCompact(windowSizeClass)) {
        TyApp(
          windowSizeClass = windowSizeClass,
          // I pass applicationContext so subs don't leak MainActivity, for now.
          appContext = applicationContext
        )
      }
    }
  }

  override fun onStart() {
    super.onStart()
    val sessionToken =
      SessionToken(this, ComponentName(this, PlaybackService::class.java))

    val controllerFuture =
      MediaController.Builder(this, sessionToken).buildAsync()
    controllerFuture.addListener(
      {
        // MediaController is available here with controllerFuture.get()
      },
      MoreExecutors.directExecutor()
    )
  }

  override fun onResume() {
    super.onResume()
    orientationEventListener = TyOrientationEventListener(this)
    orientationEventListener!!.enable()
    dispatch(v(common.expand_top_app_bar))
  }

  override fun onStop() {
    super.onStop()
    orientationEventListener?.disable()
    orientationEventListener = null
//    finish()
  }
}

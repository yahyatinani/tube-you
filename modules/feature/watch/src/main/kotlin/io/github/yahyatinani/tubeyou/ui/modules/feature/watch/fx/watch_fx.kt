package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fx

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.expand_player_sheet
import io.github.yahyatinani.recompose.RegFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun RegWatchFx(context: Activity, orientation: Int) {
  val scope = rememberCoroutineScope()

  RegFx(common.close_player) {
    scope.launch { TyPlayer.close() }
  }

  RegFx(common.toggle_player) {
    scope.launch { TyPlayer.togglePlayPause() }
  }

  RegFx("quality_list") {
    scope.launch {
      dispatch(
        v(
          "stream_panel_fsm",
          "set_quality_list",
          TyPlayer.availableResolutions(),
          TyPlayer.currentResolution()
        )
      )
    }
  }

  RegFx("set_player_resolution") { resolution ->
    scope.launch {
      TyPlayer.setResolution(resolution as Int)
    }
  }

  RegFx(id = ":toggle_orientation", key1 = orientation) {
    when (context.resources.configuration.orientation) {
      Configuration.ORIENTATION_PORTRAIT -> {
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
      }

      else -> {
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
      }
    }
  }

  RegFx(":player_fullscreen_landscape") {
    context.runOnUiThread {
      val window = context.window
      val bm = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888).apply {
        eraseColor(Color.Black.toArgb())
      }
      window.setBackgroundDrawable(BitmapDrawable(context.resources, bm))

      with(WindowCompat.getInsetsController(window, window.decorView)) {
        systemBarsBehavior =
          WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.systemBars())
      }
    }
  }

  RegFx(":player_portrait") {
    context.runOnUiThread {
      val window = context.window
      with(WindowCompat.getInsetsController(window, window.decorView)) {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        show(WindowInsetsCompat.Type.systemBars())
      }
    }
  }
}

@Composable
@kotlin.OptIn(ExperimentalMaterial3Api::class)
fun RegPlayerSheetEffects(playerSheetState: SheetState) {
  val playerSheetScope = rememberCoroutineScope()
  RegFx(expand_player_sheet) {
    playerSheetScope.launch { playerSheetState.expand() }
  }

  RegFx(common.collapse_player_sheet) {
    playerSheetScope.launch { playerSheetState.partialExpand() }
  }

  RegFx(common.hide_player_sheet) {
    playerSheetScope.launch { TyPlayer.setVolume() }
    playerSheetScope.launch {
      TyPlayer.setVolume()
      playerSheetState.hide()
      TyPlayer.close()
    }
  }
}

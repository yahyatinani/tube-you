package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer

import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.media3.common.util.UnstableApi
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.expand_player_sheet
import io.github.yahyatinani.recompose.RegFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun RegPlaybackFxs() {
  val scope = rememberCoroutineScope()

  RegFx(common.close_player) {
    scope.launch { TyPlayer.close() }
  }

  RegFx(common.toggle_player) {
    TyPlayer.togglePlayPause()
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

package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

enum class PlayerSheetState { HIDDEN, COLLAPSED, EXPANDED }

fun expandPlayerSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v(common.expand_player_sheet)))

fun hidePlayerSheet(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(common.hide_player_sheet)))

fun collapsePlayerSheet(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(common.collapse_player_sheet)))

@OptIn(ExperimentalMaterial3Api::class)
val bottomSheetMachine = m(
  PlayerSheetState.EXPANDED to m(
    SheetValue.PartiallyExpanded to m(fsm.target to PlayerSheetState.COLLAPSED),
    common.minimize_player to m(
      fsm.target to PlayerSheetState.COLLAPSED,
      fsm.actions to ::collapsePlayerSheet
    ),
    SheetValue.Hidden to m(
      fsm.target to PlayerSheetState.HIDDEN,
      fsm.actions to ::hidePlayerSheet
    )
  ),
  PlayerSheetState.COLLAPSED to m(
    common.expand_player_sheet to m(
      fsm.target to PlayerSheetState.EXPANDED,
      fsm.actions to ::expandPlayerSheet
    ),
    SheetValue.Hidden to m(
      fsm.target to PlayerSheetState.HIDDEN,
      fsm.actions to ::hidePlayerSheet
    )
  ),
  fsm.ALL to m(
    common.play_video to m(
      fsm.target to PlayerSheetState.EXPANDED,
      fsm.actions to ::expandPlayerSheet
    ),
    common.close_player to m(
      fsm.target to PlayerSheetState.HIDDEN,
      fsm.actions to ::hidePlayerSheet
    )
  )
)

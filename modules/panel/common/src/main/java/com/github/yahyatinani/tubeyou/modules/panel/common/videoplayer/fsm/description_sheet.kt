package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun expandDescriptionSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(BuiltInFx.fx to v(v("half_expand_desc_sheet")))

fun closeDescriptionSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(BuiltInFx.fx to v(v("close_desc_sheet")))

@OptIn(ExperimentalMaterial3Api::class)
val descriptionSheetMachine = m(
  null to m(),
  SheetValue.Hidden to m(),
  fsm.ALL to m(
    "half_expand_desc_sheet" to m(
      fsm.target to SheetValue.PartiallyExpanded,
      fsm.actions to ::expandDescriptionSheet
    ),
    "close_desc_sheet" to m(
      fsm.target to SheetValue.Hidden,
      fsm.actions to ::closeDescriptionSheet
    ),
    common.close_player to m(
      fsm.target to SheetValue.Hidden,
      fsm.actions to ::closeDescriptionSheet
    )
  )
)

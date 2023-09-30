package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm.ALL
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun partExpandDescriptionSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v("half_expand_desc_sheet")))

fun fullExpandDescriptionSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v("expand_desc_sheet")))

fun closeDescriptionSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v("close_desc_sheet")))

@OptIn(ExperimentalMaterial3Api::class)
val descriptionSheetMachine = m(
  null to m(),
  SheetValue.PartiallyExpanded to m(
    "toggle_desc_expansion" to m(
      target to SheetValue.Expanded,
      actions to ::fullExpandDescriptionSheet
    )
  ),
  SheetValue.Expanded to m(
    "toggle_desc_expansion" to m(
      target to SheetValue.PartiallyExpanded,
      actions to ::partExpandDescriptionSheet
    )
  ),
  ALL to m(
    "half_expand_desc_sheet" to m(
      target to SheetValue.PartiallyExpanded,
      actions to ::partExpandDescriptionSheet
    ),
    "close_desc_sheet" to m(
      target to SheetValue.Hidden,
      actions to ::closeDescriptionSheet
    ),
    common.close_player to m(
      target to SheetValue.Hidden,
      actions to ::closeDescriptionSheet
    ),
    common.play_video to m(
      target to SheetValue.Hidden,
      actions to ::closeDescriptionSheet
    )
  )
)

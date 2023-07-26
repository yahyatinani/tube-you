package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun expandCommentsSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(BuiltInFx.fx to v(v("half_expand_comments_sheet")))

fun closeCommentsSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(BuiltInFx.fx to v(v("close_comments_sheet")))

@OptIn(ExperimentalMaterial3Api::class)
val commentsSheetMachine = m(
  null to m(),
  SheetValue.Hidden to m(),
  fsm.ALL to m(
    "half_expand_comments_sheet" to m(
      fsm.target to SheetValue.PartiallyExpanded,
      fsm.actions to ::expandCommentsSheet
    ),
    "close_comments_sheet" to m(
      fsm.target to SheetValue.Hidden,
      fsm.actions to ::closeCommentsSheet
    )
  )
)

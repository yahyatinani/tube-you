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

fun expandCommentsSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v("half_expand_comments_sheet")))

fun closeCommentsSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v("close_comments_sheet")))

@OptIn(ExperimentalMaterial3Api::class)
val commentsSheetMachine = m(
  fsm.ALL to m(
    "half_expand_comments_sheet" to m(
      fsm.target to SheetValue.PartiallyExpanded,
      fsm.actions to ::expandCommentsSheet
    ),
    "close_comments_sheet" to m(
      fsm.target to SheetValue.Hidden,
      fsm.actions to ::closeCommentsSheet
    ),
    common.close_player to m(
      fsm.target to SheetValue.Hidden,
      fsm.actions to ::closeCommentsSheet
    ),
    common.play_video to m(
      fsm.target to SheetValue.Hidden,
      fsm.actions to ::closeCommentsSheet
    )
  )
)

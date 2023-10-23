package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun fullExpandCommentsSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v("expand_comments_sheet")))

fun partExpandCommentsSheet(
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
  null to m(
    "half_expand_comments_sheet" to m(
      target to "Expanding",
      actions to ::partExpandCommentsSheet
    ),
    v("comments_sheet", SheetValue.PartiallyExpanded) to m(
      target to SheetValue.PartiallyExpanded,
      actions to ::fullExpandCommentsSheet
    )
  ),
  SheetValue.Hidden to m(
    "half_expand_comments_sheet" to m(
      target to SheetValue.PartiallyExpanded,
      actions to ::partExpandCommentsSheet
    )
  ),
  "Expanding" to m(
    "half_expand_comments_sheet" to m(
      target to SheetValue.PartiallyExpanded,
      actions to ::partExpandCommentsSheet
    )
  ),
  SheetValue.PartiallyExpanded to m(
    "toggle_comments_expansion" to m(
      target to SheetValue.Expanded,
      actions to ::fullExpandCommentsSheet
    )
  ),
  SheetValue.Expanded to m(
    "toggle_comments_expansion" to m(
      target to SheetValue.PartiallyExpanded,
      actions to ::partExpandCommentsSheet
    )
  ),
  fsm.ALL to m(
    "close_comments_sheet" to m(
      target to SheetValue.Hidden,
      actions to ::closeCommentsSheet
    ),
    v("comments_sheet", SheetValue.Hidden) to m(
      target to SheetValue.Hidden,
      actions to ::closeCommentsSheet
    ),
    common.close_player to m(
      target to SheetValue.Hidden,
      actions to ::closeCommentsSheet
    ),
    common.play_video to m(target to null, actions to ::closeCommentsSheet)
  )
)

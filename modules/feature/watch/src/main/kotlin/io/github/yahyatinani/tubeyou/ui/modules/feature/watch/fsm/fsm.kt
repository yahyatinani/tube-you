package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm

import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

val streamPanelMachine = m(
  fsm.type to fsm.parallel,
  fsm.regions to v(
    v(":player", playerMachine),
    v(":player_sheet", bottomSheetMachine),
    v(":description_sheet", descriptionSheetMachine),
    v(":comments_sheet", commentsSheetMachine),
    v(":comments_list", commentsListMachine),
    v(":comment_replies", commentRepliesListMachine)
  )
)

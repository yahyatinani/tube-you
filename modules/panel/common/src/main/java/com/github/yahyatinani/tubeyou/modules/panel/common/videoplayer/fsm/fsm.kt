package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm

import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

val playbackMachine = m(
  fsm.type to fsm.parallel,
  fsm.regions to v(
    v(":player", playerMachine),
    v(":player_sheet", bottomSheetMachine),
    v(":comments_list", commentsListMachine),
    v(":comments_sheet", commentsSheetMachine),
    v(":description_sheet", descriptionSheetMachine)
  )
)

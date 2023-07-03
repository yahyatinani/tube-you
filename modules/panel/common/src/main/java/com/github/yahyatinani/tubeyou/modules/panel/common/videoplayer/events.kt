package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer

import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.regEventDb
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun regPlaybackEvents() {
  regEventFx(common.close_player) { _, _ ->
    m(BuiltInFx.fx to v(v(common.close_player)))
  }

  regEventFx(common.toggle_player) { _, _ ->
    m(BuiltInFx.fx to v(v(common.toggle_player)))
  }

  regEventDb("is_playing") { db: AppDb, (_, flag) ->
    db.assoc("is_playing", flag)
  }
}

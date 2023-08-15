package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer

import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun regPlaybackEvents() {
  regEventFx(common.close_player) { _, _ ->
    m(fx to v(v(common.close_player)))
  }

  regEventFx(common.toggle_player) { _, _ ->
    m(fx to v(v(common.toggle_player)))
  }

  regEventFx("set_player_resolution") { _, (_, resolution) ->
    m(fx to v(v("set_player_resolution", resolution)))
  }
}

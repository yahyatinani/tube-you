package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.load
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.refresh
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.set_loading_error
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.set_loading_results
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.PanelStates.FAILED
import com.github.yahyatinani.tubeyou.modules.panel.common.PanelStates.LOADED
import com.github.yahyatinani.tubeyou.modules.panel.common.PanelStates.LOADING
import com.github.yahyatinani.tubeyou.modules.panel.common.PanelStates.REFRESHING
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fx.BuiltInFx.dispatch
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun loadHomeContent(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(dispatch, v(home.load_trending))))

fun setHomeContent(appDb: AppDb, state: State?, event: Event): Effects =
  m(fsm.state_map to state!!.assoc(home.content, event[1]))

val homeMachine = m<Any?, Any?>(
  null to m(load to m(target to LOADING, actions to ::loadHomeContent)),
  LOADING to m(
    set_loading_results to m(target to LOADED, actions to ::setHomeContent),
    set_loading_error to m(target to FAILED, actions to ::setHomeContent)
  ),
  REFRESHING to m(
    set_loading_results to m(target to LOADED, actions to ::setHomeContent),
    set_loading_error to m(target to FAILED, actions to ::setHomeContent)
  ),
  LOADED to m(refresh to m(target to REFRESHING, actions to ::loadHomeContent)),
  FAILED to m(refresh to m(target to REFRESHING, actions to ::loadHomeContent))
)

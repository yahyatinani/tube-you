package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.BuiltInFx.dispatch
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.fx.Effects
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
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
import com.github.yahyatinani.tubeyou.modules.panel.common.State
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm.actions
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm.target

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

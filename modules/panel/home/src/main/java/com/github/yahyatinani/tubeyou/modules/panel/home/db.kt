package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.ids.coeffects
import com.github.whyrising.recompose.ids.recompose
import com.github.whyrising.y.core.get
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.panel.common.States
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import kotlinx.coroutines.CoroutineScope

// -- Spec ---------------------------------------------------------------------

/**
 * {:state [States.Loading]
 * :home/popular_vids (
 * [com.github.yahyatinani.tubeyou.modules.panel.common.Video])
 * :search_bar ({:query "" :suggestions [] :results []}, ...)
 * }
 */

// -- Cofx Registrations -------------------------------------------------------

fun getRegHomeCofx(scope: CoroutineScope) {
  regCofx(home.fsm_next_state) { cofx ->
    val e = cofx[coeffects.originalEvent] as Event
    cofx.assoc(recompose.db, handleNextState(appDbBy(cofx), e))
  }

  regCofx(home.coroutine_scope) { cofx ->
    cofx.assoc(home.coroutine_scope, scope)
  }
}

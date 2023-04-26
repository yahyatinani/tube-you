package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.ids.coeffects
import com.github.whyrising.recompose.ids.recompose
import com.github.whyrising.y.core.get
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.panel.common.States
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy

// -- Spec ---------------------------------------------------------------------

/**
 * {:state [States.Loading]
 * :home/popular_vids (
 * [com.github.yahyatinani.tubeyou.modules.panel.common.Video])
 * :search_bar ({:query "" :suggestions [] :results []}, ...)
 * }
 */

// -- Cofx Registrations -------------------------------------------------------

val regHomeCofx = regCofx(home.fsm) { cofx ->
  val (eventId) = cofx[coeffects.originalEvent] as Event
  cofx.assoc(recompose.db, updateToNextState(appDbBy(cofx), eventId))
}

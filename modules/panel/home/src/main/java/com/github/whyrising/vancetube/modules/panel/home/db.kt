package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.ids.coeffects
import com.github.whyrising.recompose.ids.recompose
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.panel.common.States
import com.github.whyrising.vancetube.modules.panel.common.appDbBy
import com.github.whyrising.y.core.get

// -- Spec ---------------------------------------------------------------------

/**
 * {:state [States.Loading]
 * :home/popular_vids (
 * [com.github.whyrising.vancetube.modules.panel.common.Video])
 * :search_bar ({:query "" :suggestions [] :results []}, ...)
 * }
 */

// -- Cofx Registrations -------------------------------------------------------

val regHomeCofx = regCofx(home.fsm) { cofx ->
  val (eventId) = cofx[coeffects.originalEvent] as Event
  cofx.assoc(recompose.db, updateToNextState(appDbBy(cofx), eventId))
}

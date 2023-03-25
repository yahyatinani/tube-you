package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.ids.coeffects
import com.github.whyrising.recompose.ids.recompose
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.panel.common.States
import com.github.whyrising.vancetube.modules.panel.common.appDbBy
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

// -- Spec ---------------------------------------------------------------------

/**
 * {:state [States.Loading]
 * :home/popular_vids (
 * [com.github.whyrising.vancetube.modules.panel.common.VideoData])
 * :home/search_bar {:query "" :isActive false :suggestions []}
 * }
 */

// -- Cofx Registrations -------------------------------------------------------

val regHomeCofx = regCofx(home.fsm) { cofx ->
  val (eventId) = cofx[coeffects.originalEvent] as Event
  val nextDb = updateToNextState(appDbBy(cofx), eventId)

  cofx.assoc(
    recompose.db, assocIn(
      nextDb,
      l(home.panel, ":home/search_bar"),
      m(
        ":query" to "",
        ":isActive" to true,
        ":suggestions" to v("suggestion1", "suggestion2", "suggestion3")
      )
    )
  )
}

package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.whyrising.recompose.cofx.regCofx
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.panel.common.search.Video
import kotlinx.coroutines.CoroutineScope

// -- Spec ---------------------------------------------------------------------

/**
 * {:home/state ([PanelStates.LOADED] , value)
 *  :home/content ([Video])
 *  :search_bar ({:query "" :suggestions [] :results []}, ...)
 * }
 */

// -- Cofx Registrations -------------------------------------------------------

fun getRegHomeCofx(scope: CoroutineScope) {
  regCofx(home.coroutine_scope) { cofx ->
    cofx.assoc(home.coroutine_scope, scope)
  }
}

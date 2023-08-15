package com.github.yahyatinani.tubeyou

import androidx.navigation.navOptions
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_navigation_item
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_backstack_empty
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigate_to
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.start_destination
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.letIf
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.ids.recompose.db
import io.github.yahyatinani.recompose.regEventDb
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

typealias AppDb = IPersistentMap<Any, Any>

fun regAppEvents() {
  regEventDb<AppDb>(
    id = common.initialize
  ) { _, _ ->
    /*
    val isOnline = cofx[is_online]!! as Boolean
  m<Any, Any>(
    db to defaultDb,
    fx to v(if (isOnline) null else v(navigate_to, LIBRARY_GRAPH_ROUTE))
  )
 */
    defaultDb
  }

  // TODO: rethink this event handler
  regEventFx(
    id = active_navigation_item,
    interceptors = v(injectCofx(is_backstack_empty))
  ) { cofx, (_, destination) ->
    m(
      db to appDbBy(cofx)
        .assoc(is_backstack_empty, cofx[is_backstack_empty] as Boolean)
        .letIf(navItems[destination] != null) {
          it.assoc(active_navigation_item, destination)
        }
    )
  }

  regEventFx(
    id = common.on_click_nav_item,
    interceptors = v(injectCofx(start_destination))
  ) { cofx, (_, destination) ->
    // TODO: Set active_panel to active_navigation_item
    val appDb = appDbBy(cofx)
    m<Any, Any>(
      db to appDb.assoc(active_navigation_item, destination),
      fx to v(
        if (destination == appDb[active_navigation_item]) {
          // TODO: Use one fx for all panels to scroll up by overriding reg fx
          v(home.go_top_list)
        } else {
          v(
            navigate_to,
            m(
              common.destination to destination,
              common.navOptions to navOptions {
                popUpTo(cofx[start_destination] as Int) { saveState = true }
                restoreState = true
              }
            )
          )
        }
      )
    )
  }

  regEventFx(common.bottom_bar_back_press) { _, _ ->
    m<Any, Any>(fx to v(v(common.bottom_bar_back_press)))
  }
}

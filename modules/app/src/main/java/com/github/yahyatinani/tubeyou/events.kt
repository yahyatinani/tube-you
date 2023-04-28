package com.github.yahyatinani.tubeyou

import androidx.navigation.navOptions
import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_navigation_item
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_backstack_empty
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigate_to
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.start_destination
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.letIf

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
        } else v(
          navigate_to,
          m(
            common.destination to destination,
            common.navOptions to navOptions {
              popUpTo(cofx[start_destination] as Int) { saveState = true }
              restoreState = true
            }
          )
        )
      )
    )
  }

  regEventFx(common.back_press) { _, _ ->
    m<Any, Any>(fx to v(v(common.back_press)))
  }
}

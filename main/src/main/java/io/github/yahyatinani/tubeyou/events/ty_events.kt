package io.github.yahyatinani.tubeyou.events

import androidx.navigation.navOptions
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.back_press_top_nav
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_top_backstack_empty
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.prev_top_nav_route
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.start_destination
import io.github.yahyatinani.recompose.cofx.Coeffects
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.ids.recompose.db
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.tubeyou.db.TyAppState
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun appDbBy(cofx: Coeffects): TyAppState = get(cofx, db)!!

fun regTyEvents() {
  regEventFx(
    id = common.on_click_nav_item,
    interceptors = v(injectCofx(start_destination))
  ) { cofx: Coeffects, (_, navItemRoute) ->
    val appDb = appDbBy(cofx)
    if (appDb.activeTopLevelRoute === navItemRoute) {
      // TODO: scroll up to top list.
      return@regEventFx m<Any, Any>()
    }
    m(
      db to appDb.copy(
        activeTopLevelRoute = navItemRoute as String,
        topLevelBackHandlerEnabled = true
      ),
      fx to v(
        v(
          common.navigate_to,
          m(
            common.destination to navItemRoute,
            common.navOptions to navOptions {
              popUpTo(cofx[start_destination] as Int) {
                saveState = true
              }
              restoreState = true
            }
          )
        )
      )
    )
  }

  regEventFx(
    id = back_press_top_nav,
    interceptors = v(
      injectCofx(prev_top_nav_route),
      injectCofx(is_top_backstack_empty)
    )
  ) { cofx: Coeffects, _ ->
    val prevDestinationRoute = cofx[prev_top_nav_route]
    val isTopLevelBackstackEmpty = get<Boolean>(cofx, is_top_backstack_empty)!!
    val disabled =
      prevDestinationRoute == HOME_GRAPH_ROUTE && isTopLevelBackstackEmpty

    m<Any, Any>(
      db to appDbBy(cofx).copy(
        activeTopLevelRoute = prevDestinationRoute as String,
        topLevelBackHandlerEnabled = !disabled
      ),
      fx to v(v(back_press_top_nav, prevDestinationRoute))
    )
  }
}

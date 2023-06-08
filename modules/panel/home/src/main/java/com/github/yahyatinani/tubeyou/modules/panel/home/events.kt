package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.go_top_list
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.load_trending
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.ktor
import com.github.yahyatinani.tubeyou.modules.panel.common.search.Video
import com.github.yahyatinani.tubeyou.modules.panel.common.trigger
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

fun regHomeEvents() {
  regEventFx(
    id = load_trending,
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val popularVideosEndpoint = "${appDb[common.api_url]}/trending?region=US"
    m<Any, Any>(
      db to appDb,
      fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to popularVideosEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[home.coroutine_scope],
            ktor.response_type_info to typeInfo<PersistentVector<Video>>(),
            ktor.on_success to v(home.fsm, home.set_loading_results),
            ktor.on_failure to v(home.fsm, home.set_loading_error)
          )
        )
      )
    )
  }

  regEventFx(go_top_list) { _, _ ->
    m(fx to v(v(go_top_list)))
  }

  val homeStatePath = v(HOME_GRAPH_ROUTE, home.fsm_state)
  regEventFx(id = home.fsm) { cofx, e ->
    val appDb = appDbBy(cofx)
    trigger(homeMachine, m(db to appDb), homeStatePath, e.subvec(1, e.count))
  }
}

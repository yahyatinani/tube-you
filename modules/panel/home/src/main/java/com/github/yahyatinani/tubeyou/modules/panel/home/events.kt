package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.go_top_list
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.load_trending
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.search.Video
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fsm.trigger
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.ids.recompose.db
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

fun regHomeEvents() {
  regEventFx(
    id = load_trending,
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val popularVideosEndpoint = "${appDb[common.api_url]}/trending?region=RU"
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

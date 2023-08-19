package io.github.yahyatinani.tubeyou.modules.feature.home.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.clearEvent
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fsm.trigger
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.tubeyou.common.appDbBy
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.tubeyou.modules.core.network.Video
import io.github.yahyatinani.tubeyou.modules.feature.home.fsm.homeMachine
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

@Composable
fun RegHomeEvents() {
  regEventFx(
    id = home.load,
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val popularVideosEndpoint = "${appDb[ty_db.api_url]}/trending?region=US"
    m<Any, Any>(
      recompose.db to appDb,
      BuiltInFx.fx to v(
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

  regEventFx(home.go_top_list) { _, _ ->
    m(BuiltInFx.fx to v(v(home.go_top_list)))
  }

  val homeStatePath = v(HOME_GRAPH_ROUTE, home.fsm_state)

  regEventFx(id = home.fsm) { cofx, e ->
    val appDb = appDbBy(cofx)
    trigger(
      homeMachine,
      m(recompose.db to appDb),
      homeStatePath,
      e.subvec(1, e.count)
    )
  }

  DisposableEffect(Unit) {
    onDispose {
      clearEvent(home.load)
      clearEvent(home.go_top_list)
      clearEvent(home.fsm)
    }
  }
}

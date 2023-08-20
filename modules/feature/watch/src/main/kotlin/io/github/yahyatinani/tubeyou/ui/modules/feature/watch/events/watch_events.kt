package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.clearEvent
import io.github.yahyatinani.recompose.cofx.Coeffects
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fsm.trigger
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.recompose.pagingfx.paging
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.tubeyou.common.appDbBy
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamComments
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamData
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.streamPanelMachine
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

@Composable
fun RegWatchEvents() {
  DisposableEffect(Unit) {
    regEventFx(id = "stream_panel_fsm") { cofx, e ->
      val appDb = appDbBy(cofx)
      trigger(
        streamPanelMachine,
        m(recompose.db to appDb),
        v("stream_panel_fsm"),
        e.subvec(1, e.count)
      )
    }

    regEventFx(
      id = "load_stream",
      interceptors = v(injectCofx("player_scope"))
    ) { cofx: Coeffects, (_, videoId) ->
      val appDbBy = appDbBy(cofx)
      val appDb = appDbBy.dissoc("current_video_stream")
      val id = (videoId as String).replace("/watch?v=", "")
      m(
        BuiltInFx.fx to v(
          v(
            ktor.http_fx,
            m(
              ktor.method to HttpMethod.Get,
              ktor.url to "${appDb[ty_db.api_url]}/streams/$id",
              ktor.timeout to 8000,
              ktor.coroutine_scope to cofx["player_scope"],
              ktor.response_type_info to typeInfo<StreamData>(),
              ktor.on_success to v("stream_panel_fsm", "launch_stream"),
              ktor.on_failure to v("todo")
            )
          )
        )
      )
    }

    regEventFx(
      id = "load_stream_comments",
      interceptors = v(injectCofx("player_scope"))
    ) { cofx: Coeffects, (_, videoId) ->
      val id = (videoId as String).replace("/watch?v=", "")
      val api = appDbBy(cofx)[ty_db.api_url]
      val commentsEvent = v("stream_panel_fsm", "set_stream_comments")
      m(
        BuiltInFx.fx to v(
          v(
            paging.fx,
            m(
              ktor.method to HttpMethod.Get,
              ktor.url to "$api/comments/$id",
              ktor.timeout to 8000,
              "pageName" to "nextpage",
              "nextUrl" to "$api/nextpage/comments/$id",
              "eventId" to "load_stream_comments",
              paging.append_id to "append_comments",
              ktor.coroutine_scope to cofx["player_scope"],
              ktor.response_type_info to typeInfo<StreamComments>(),
              ktor.on_success to commentsEvent,
              paging.on_page_success to v(
                "stream_panel_fsm",
                "append_comments_page"
              ),
              "on_appending" to v("stream_panel_fsm"),
              ktor.on_failure to v(""),
              paging.page_size to 5
            )
          )
        )
      )
    }

    regEventFx(
      id = "load_comment_replies",
      interceptors = v(injectCofx("player_scope"))
    ) { cofx: Coeffects, (_, videoId, repliesPage) ->
      val api = appDbBy(cofx)[ty_db.api_url]
      val commentsEndpoint =
        "$api/nextpage/comments/$videoId?nextpage=$repliesPage"
      m(
        BuiltInFx.fx to v(
          v(
            paging.fx,
            m(
              ktor.method to HttpMethod.Get,
              ktor.url to commentsEndpoint,
              ktor.timeout to 8000,
              "pageName" to "nextpage",
              "nextUrl" to "$api/nextpage/comments/$videoId",
              "eventId" to "load_comment_replies",
              ktor.coroutine_scope to cofx["player_scope"],
              ktor.response_type_info to typeInfo<StreamComments>(),
              ktor.on_success to v("ignore"),
              ktor.on_failure to v(""),
              paging.on_page_success to v(
                "stream_panel_fsm",
                "append_replies_page"
              ),
              paging.append_id to "append_replies",
              "on_appending" to v("stream_panel_fsm"),
              paging.page_size to 5
            )
          )
        )
      )
    }

    onDispose {
      clearEvent("stream_panel_fsm")
      clearEvent("load_stream")
      clearEvent("load_stream_comments")
      clearEvent("load_comment_replies")
    }
  }
}

@Composable
fun RegPlaybackEvents() {
  DisposableEffect(Unit) {
    regEventFx(common.close_player) { _, _ ->
      m(BuiltInFx.fx to v(v(common.close_player)))
    }

    regEventFx(common.toggle_player) { _, _ ->
      m(BuiltInFx.fx to v(v(common.toggle_player)))
    }

    regEventFx("set_player_resolution") { _, (_, resolution) ->
      m(BuiltInFx.fx to v(v("set_player_resolution", resolution)))
    }

    regEventFx(":player_fullscreen_landscape") { _, _ ->
      m(BuiltInFx.fx to v(v(":player_fullscreen_landscape")))
    }

    regEventFx(":player_portrait") { _, _ ->
      m(BuiltInFx.fx to v(v(":player_portrait")))
    }

    regEventFx(":toggle_orientation") { _, _ ->
      m(BuiltInFx.fx to v(v(":toggle_orientation")))
    }

    onDispose {
      clearEvent(common.close_player)
      clearEvent(common.toggle_player)
      clearEvent("set_player_resolution")
      clearEvent(":player_fullscreen_landscape")
      clearEvent(":player_portrait")
      clearEvent(":toggle_orientation")
    }
  }
}

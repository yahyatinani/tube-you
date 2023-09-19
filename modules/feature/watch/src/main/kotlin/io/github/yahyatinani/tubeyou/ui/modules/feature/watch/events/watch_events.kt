package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.clearEvent
import io.github.yahyatinani.recompose.cofx.Coeffects
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fsm.trigger
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.recompose.pagingfx.paging
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.tubeyou.common.appDbBy
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamComments
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamData
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.streamPanelMachine
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen.lerp
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

@Composable
fun RegWatchEvents() {
  DisposableEffect(Unit) {
    regEventFx(id = "stream_panel_fsm") { cofx, e ->
      println("stream_panel_fsm $e")
      val appDb = appDbBy(cofx)
      trigger(
        machine = streamPanelMachine,
        fxs = m(recompose.db to appDb),
        statePath = v("stream_panel_fsm"),
        event = e.subvec(1, e.count)
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
        fx to v(
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
      m(
        fx to v(
          v(
            paging.fx,
            m(
              ktor.method to HttpMethod.Get,
              ktor.url to "$api/comments/$id",
              ktor.timeout to 8000,
              ktor.coroutine_scope to cofx["player_scope"],
              ktor.response_type_info to typeInfo<StreamComments>(),
              ktor.on_success to v("stream_panel_fsm", "set_stream_comments"),
              paging.pageName to "nextpage",
              paging.nextUrl to "$api/nextpage/comments/$id",
              paging.trigger_append_id to "append_comments",
              paging.on_append to v("stream_panel_fsm"),
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
        fx to v(
          v(
            paging.fx,
            m(
              ktor.method to HttpMethod.Get,
              ktor.url to commentsEndpoint,
              ktor.timeout to 8000,
              ktor.coroutine_scope to cofx["player_scope"],
              ktor.response_type_info to typeInfo<StreamComments>(),
              paging.pageName to "nextpage",
              paging.nextUrl to "$api/nextpage/comments/$videoId",
              paging.trigger_append_id to "append_replies",
              paging.on_append to v("stream_panel_fsm"),
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
      m(fx to v(v(common.hide_player_sheet), v(common.close_player)))
    }

    regEventFx(common.toggle_player) { _, _ ->
      m(fx to v(v(common.toggle_player)))
    }

    regEventFx("set_player_resolution") { _, (_, resolution) ->
      m(fx to v(v("set_player_resolution", resolution)))
    }

    regEventFx(":player_fullscreen_landscape") { _, _ ->
      m(fx to v(v(":player_fullscreen_landscape")))
    }

    regEventFx(":player_portrait") { _, _ ->
      m(fx to v(v(":player_portrait")))
    }

    regEventFx(":toggle_orientation") { _, _ ->
      m(fx to v(v(":toggle_orientation")))
    }

    regEventFx("set_volume") { _,
      (
        _,
        sheetOffsetPx,
        screenHeightPx,
        bottomBar,
        miniPlayerHeightPx
      ) ->
      val sheetYOffset = (
        (sheetOffsetPx as Float) -
          ((screenHeightPx as Float) - (bottomBar as Float))
        ).coerceAtLeast(
        0f
      )
      val vol = lerp(
        sheetYOffset = sheetYOffset,
        traverse = miniPlayerHeightPx as Float,
        start = 1f,
        end = .1f
      )
      m(fx to v(v("set_volume", vol)))
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

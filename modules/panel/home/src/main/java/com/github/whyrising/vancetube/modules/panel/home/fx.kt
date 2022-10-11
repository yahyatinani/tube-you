package com.github.whyrising.vancetube.modules.panel.home

import android.util.Log
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.regFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.panel.common.VideoData
import com.github.whyrising.vancetube.modules.panel.common.client
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.v
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.UnknownHostException

fun regHomeFx(scope: CoroutineScope) {
  Log.i("regHomeFx", "init")

  regFx(home.load_popular_videos) { api ->
    scope.launch {
      val endpoint = "$api/popular?fields=videoId,title,videoThumbnails," +
        "lengthSeconds,viewCount,author,publishedText,authorId"
      Log.i("Endpoint", endpoint)
      try {
        val httpResponse = client.get(endpoint)
        Log.i("httpResponse", "call ended!")

        when (httpResponse.status.value) {
          403 -> TODO("403 Forbidden")
          429 -> TODO("403 Too many requests")
          502 -> TODO("502 Bad Gateway")
        }
        val popularVideos = httpResponse.body<PersistentVector<VideoData>>()

        dispatch(v(home.set_popular_vids, popularVideos))
      } catch (e: UnknownHostException) {
        // TODO: when no WIFI/Network
        Log.e("UnknownHostException", "$e")
      } catch (e: HttpRequestTimeoutException) {
        TODO("HttpRequestTimeoutException : ${e.message}")
      } catch (e: NoTransformationFoundException) {
        TODO("504 Gateway Time-out: $e")
      } catch (e: Exception) {
        throw e
      }
    }
  }
}

fun regScrollToTopListFx(
  scope: CoroutineScope,
  scrollToTop: suspend () -> Unit
) {
  regFx(home.go_top_list) {
    scope.launch {
      dispatch(v(common.expand_top_app_bar))
      scrollToTop()
    }
  }
}

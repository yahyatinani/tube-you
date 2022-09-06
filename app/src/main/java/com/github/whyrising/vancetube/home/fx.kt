package com.github.whyrising.vancetube.home

import android.util.Log
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.regFx
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.v
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.UnknownHostException

val client = HttpClient(Android) {
  install(Logging) {
    logger = Logger.DEFAULT
    level = LogLevel.ALL
    filter { request ->
      request.url.host.contains("invidious.tiekoetter.com") ||
        request.url.host.contains("invidious.namazso.eu") ||
        request.url.host.contains("y.com.sb")
    }
  }
  install(HttpTimeout)
  install(ContentNegotiation) {
    json(
      Json {
        isLenient = true
        ignoreUnknownKeys = true
      }
    )
  }
}

fun regHomeFx(scope: CoroutineScope) {
  regFx(home.load) { api ->
    scope.launch(Dispatchers.IO) {
      val endpoint = "$api/popular?fields=videoId,title,videoThumbnails," +
        "lengthSeconds,viewCount,author,publishedText,authorId"
      Log.i("API", endpoint)
      try {
        val httpResponse = client.get(endpoint)
        Log.i("httpResponse", "call ended!")

        when (httpResponse.status.value) {
          403 -> TODO("403 Forbidden")
          429 -> TODO("403 Too many requests")
          502 -> TODO("502 Bad Gateway")
        }
        val popularVideos = httpResponse
          .body<PersistentVector<VideoData>>()

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

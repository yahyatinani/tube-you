package com.github.whyrising.vancetube.modules.panel.common

import android.util.Log
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.regFx
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
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
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
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

// -- Effects ------------------------------------------------------------------

fun httpEffect(requestMap: Any?) {
  // TODO: 1. validate requestMap.
  requestMap as IPersistentMap<Any, Any>
  val uri = requestMap["uri"] as String
  val timeout = requestMap["timeout"]
  val responseFormat = requestMap["response_format"]
  val onSuccess = requestMap["on_success"]!!
  val onFailure = requestMap["on_failure"]
  Log.i("Endpoint", "httpResponse.toString()")
  runBlocking {
    val httpResponse = client.get {
      url(uri)
      if (timeout != null) timeout {
        requestTimeoutMillis = timeout as Long?
      }
    }
    dispatch(v(onSuccess, httpResponse.body<JsonElement>()))
  }
}

@Suppress("EnumEntryName", "ClassName")
enum class ktor {
  uri,
  timeout,
  on_success,
  on_failure,
  method,
  get,
  response_type_info,
  http_fx,
  exec_scope;

  override fun toString(): String = name
}

fun regHttpKtor(globalScope: CoroutineScope) {
  regFx(ktor.http_fx) { request ->
    (get<CoroutineScope>(request, ktor.exec_scope) ?: globalScope).launch {
      request as IPersistentMap<Any, Any>
      // TODO: 1. validate(request).

      val method = request[ktor.method] as ktor
      val uri = get<String>(request, ktor.uri)!!
      val responseTypeInfo = get<TypeInfo>(request, ktor.response_type_info)!!
      val timeout = request[ktor.timeout]
      val onSuccess = get<Event>(request, ktor.on_success)!!
      val onFailure = get<Event>(request, ktor.on_failure)!!

      try {
        val httpResponse = client.get {
          url(uri)
          timeout {
            requestTimeoutMillis = (timeout as Number?)?.toLong()
          }
        }

        dispatch(onSuccess.conj(httpResponse.call.body(responseTypeInfo)))
        // TODO: dispatch on Failure
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

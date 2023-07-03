package com.github.yahyatinani.tubeyou.modules.panel.common

import com.github.yahyatinani.tubeyou.modules.panel.common.search.searchModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val tyHttpClient = HttpClient(Android) {
  install(Logging) {
    logger = Logger.DEFAULT
    level = LogLevel.ALL
    filter { request ->
      request.url.host.contains("piped")
    }
  }
  install(HttpTimeout)
  install(ContentNegotiation) {
    json(
      Json {
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule = searchModule
      }
    )
  }
}

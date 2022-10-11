package com.github.whyrising.vancetube.modules.panel.common

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

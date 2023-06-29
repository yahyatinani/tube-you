package com.github.yahyatinani.tubeyou.modules.panel.common

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.github.yahyatinani.tubeyou.modules.panel.common.search.LazyPagingItems2
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchResponse
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchResult
import com.github.yahyatinani.tubeyou.modules.panel.common.search.searchModule
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.regFx
import io.github.yahyatinani.y.concurrency.Atom
import io.github.yahyatinani.y.concurrency.atom
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.ktor.client.HttpClient
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
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.net.UnknownHostException

val myClient = HttpClient(Android) {
  install(Logging) {
    logger = Logger.DEFAULT
    level = LogLevel.ALL
    filter { request ->
      request.url.host.contains("pipedapi.palveluntarjoaja.eu")
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

fun regPagingFx() {
  regFx("paging", ::pagingEffect)
}

// -- Paging -------------------------------------------------------------------

val pagingSrcCache: Atom<IPersistentMap<Any, Any>> = atom(m())

class PagingSourceString(
  val httpCall: suspend (params: LoadParams<String>) -> SearchResponse
) : PagingSource<String, SearchResult>() {
  override fun getRefreshKey(state: PagingState<String, SearchResult>) =
    state.anchorPosition?.let { i -> state.closestPageToPosition(i)?.nextKey }

  override suspend fun load(
    params: LoadParams<String>
  ): LoadResult<String, SearchResult> {
    val sr: SearchResponse = httpCall(params)
    val nextpage = sr.nextpage
    return LoadResult.Page(
      data = sr.items,
      prevKey = null,
      nextKey = if (nextpage != "null" && nextpage != null) {
        URLEncoder.encode(nextpage, "UTF-8")
      } else null
    )
  }
}

private const val INITIAL_KEY = "INITIAL_KEY"

fun pagingEffect(request: Any?) {
  val coroutineScope = get<CoroutineScope>(request, ktor.coroutine_scope)!!
  val job = coroutineScope.launch {
    val onFailure = get<Event>(request, ktor.on_failure)!!
    val eventId = get<Any>(request, "eventId")!!
    val pager =
      Pager(PagingConfig(pageSize = 10), initialKey = INITIAL_KEY) {
        PagingSourceString { params: LoadParams<String> ->
          return@PagingSourceString try {
            // TODO: 1. validate(request).
            request as IPersistentMap<Any, Any>

            val nextPageKey = params.key
            val url = when {
              nextPageKey === INITIAL_KEY -> get<String>(
                request,
                ktor.url
              )!!

              else -> {
                "${request["nextUrl"]}&${request["pageName"]}=$nextPageKey"
              }
            }

            val timeout = request[ktor.timeout]
            val method = request[ktor.method] as HttpMethod // TODO:
            val httpResponse = myClient.get {
              url(url)
              timeout {
                requestTimeoutMillis =
                  (timeout as Number?)?.toLong()
              }
            }

            if (httpResponse.status == HttpStatusCode.OK) {
              val responseTypeInfo =
                get<TypeInfo>(request, ktor.response_type_info)!!
              httpResponse.call.body(responseTypeInfo) as SearchResponse
            } else {
              TODO("${httpResponse.status}, $url")
            }
          } catch (e: Exception) {
            val status = when (e) {
              is UnknownHostException -> 0
              is HttpRequestTimeoutException -> -1
              /*
                catch (e: NoTransformationFoundException) {
                    TODO("504 Gateway Time-out: $e")
                } */
              else -> throw e
            }
            TODO()
          }
        } as PagingSource<Any, Any>
      }

    val lazyPagingItems = LazyPagingItems2(
      flow = pager.flow.cachedIn(coroutineScope),
      onSuccessEvent = get<Event>(request, ktor.on_success)!!,
      onAppendEvent = get<Event>(request, "on_appending")!!
    )
    launch { lazyPagingItems.collectPagingData() }
      .invokeOnCompletion { lazyPagingItems.clear() }
    launch { lazyPagingItems.collectLoadState() }
      .invokeOnCompletion { lazyPagingItems.clear() }
  }
}

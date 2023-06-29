package com.github.yahyatinani.tubeyou.modules.panel.common.search

import androidx.compose.runtime.Immutable
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

typealias AppDb = IPersistentMap<Any, Any>

val defaultSb: IPersistentMap<Any, Any> = m(
  searchBar.query to "",
  searchBar.suggestions to v<String>()
)

val searchModule = SerializersModule {
  polymorphic(
    baseClass = SearchResult::class,
    actualClass = Video::class,
    actualSerializer = Video.serializer()
  )
  polymorphic(
    baseClass = SearchResult::class,
    actualClass = Channel::class,
    actualSerializer = Channel.serializer()
  )
  polymorphic(
    baseClass = SearchResult::class,
    actualClass = Playlist::class,
    actualSerializer = Playlist.serializer()
  )
}

@Serializable
sealed interface SearchResult

@Immutable
@Serializable
@SerialName("stream")
data class Video(
  val url: String,
  val title: String,
  val thumbnail: String,
  val uploaderName: String? = null,
  val uploaderUrl: String? = null,
  val uploaderAvatar: String? = null,
  val uploadedDate: String? = null,
  val shortDescription: String? = null,
  val duration: Long,
  val views: Long? = null,
  val uploaded: Long,
  val uploaderVerified: Boolean = false,
  val isShort: Boolean = false
) : SearchResult

@Immutable
@SerialName("channel")
@Serializable
data class Channel(
  val url: String,
  val name: String,
  val thumbnail: String,
  val subscribers: Int,
  val videos: Int,
  val description: String? = null,
  val verified: Boolean
) : SearchResult

@Immutable
@SerialName("playlist")
@Serializable
data class Playlist(
  val url: String,
  val name: String,
  val thumbnail: String,
  val uploaderName: String,
  val uploaderUrl: String,
  val uploaderVerified: Boolean,
  val playlistType: String,
  val videos: Int
) : SearchResult

@Serializable
data class SearchResponse(
  val items: PersistentVector<SearchResult>,
  val nextpage: String? = null,
  val suggestion: String? = null,
  val corrected: Boolean = false
)

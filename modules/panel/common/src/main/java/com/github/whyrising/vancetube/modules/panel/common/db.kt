package com.github.whyrising.vancetube.modules.panel.common

import androidx.compose.runtime.Immutable
import com.github.whyrising.vancetube.modules.core.keywords.searchBar
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

typealias AppDb = IPersistentMap<Any, Any>

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
data class ThumbnailData(val url: String, val quality: String? = null)

@Immutable
@Serializable
@SerialName("video")
data class Video(
  val title: String,
  val videoId: String,
  val lengthSeconds: Int,
  val videoThumbnails: List<ThumbnailData>,

  val author: String? = null,
  val authorId: String? = null,
//    val authorUrl: String,
  val description: String? = null,
  val descriptionHtml: String? = null,
  val viewCount: Long? = null,
//    val published: Long,
  val publishedText: String? = null,
  val liveNow: Boolean? = null,
  val premium: Boolean? = null,
  val isUpcoming: Boolean = false,
  val premiereTimestamp: Long? = null
) : SearchResult

@Serializable
sealed interface SearchResult

@Immutable
@SerialName("channel")
@Serializable
data class Channel(
  val author: String,
  val authorId: String,
  val authorUrl: String,
  val authorThumbnails: List<ThumbnailData>,
  val subCount: Int,
  val videoCount: Int,
  val description: String,
  val descriptionHtml: String
) : SearchResult

@Immutable
@SerialName("playlist")
@Serializable
data class Playlist(
  val title: String,
  val playlistId: String,
  val playlistThumbnail: String,
  val author: String,
  val authorId: String,
  val authorUrl: String,
  val videoCount: Int,
  val videos: List<Video>
) : SearchResult

@Serializable
data class Suggestions(
  @SerialName("suggestions")
  val value: PersistentVector<String>
)

val defaultSb: IPersistentMap<Any, Any> = m(
  searchBar.query to "",
  searchBar.suggestions to v<String>()
)

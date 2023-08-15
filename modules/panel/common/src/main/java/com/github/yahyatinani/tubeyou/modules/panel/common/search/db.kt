package com.github.yahyatinani.tubeyou.modules.panel.common.search

import androidx.compose.runtime.Immutable
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import io.github.yahyatinani.recompose.pagingfx.Page
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import java.net.URLEncoder

typealias AppDb = IPersistentMap<Any, Any>

val defaultSb: IPersistentMap<Any, Any> = m(
  searchBar.query to "",
  searchBar.suggestions to v<String>()
)

val searchModule = SerializersModule {
  polymorphic(
    baseClass = Item::class,
    actualClass = Video::class,
    actualSerializer = Video.serializer()
  )
  polymorphic(
    baseClass = Item::class,
    actualClass = Channel::class,
    actualSerializer = Channel.serializer()
  )
  polymorphic(
    baseClass = Item::class,
    actualClass = Playlist::class,
    actualSerializer = Playlist.serializer()
  )
}

@Serializable
sealed interface Item

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
) : Item

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
) : Item

@Immutable
@SerialName("playlist")
@Serializable
data class Playlist(
  val url: String,
  val name: String,
  val thumbnail: String,
  val uploaderName: String,
  val uploaderUrl: String? = null,
  val uploaderVerified: Boolean,
  val playlistType: String,
  val videos: Int
) : Item

@Serializable
data class SearchResponse(
  val items: PersistentVector<Item>,
  val nextpage: String? = null,
  val suggestion: String? = null,
  val corrected: Boolean = false
) : Page {
  override val data: List<Item> = items
  override val prevKey: String? = null
  override val nextKey: String? = if (nextpage != "null" && nextpage != null) {
    URLEncoder.encode(nextpage, "UTF-8")
  } else {
    null
  }
}

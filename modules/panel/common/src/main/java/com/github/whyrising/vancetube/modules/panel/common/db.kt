package com.github.whyrising.vancetube.modules.panel.common

import androidx.compose.runtime.Immutable
import com.github.whyrising.vancetube.modules.core.keywords.searchBar
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias AppDb = IPersistentMap<Any, Any>

@Serializable
data class ThumbnailData(val url: String)

@Immutable
@Serializable
data class VideoData(
  val videoId: String,
  val title: String,
  val videoThumbnails: List<ThumbnailData>,
  val lengthSeconds: Long,
  val author: String,
  val authorId: String,
  val viewCount: Long,
  val publishedText: String
)

@Serializable
data class Suggestions(
  @SerialName("suggestions")
  val value: PersistentVector<String>
)

val defaultSb: IPersistentMap<Any, Any> = m(
  searchBar.query to "",
  searchBar.suggestions to v<String>()
)

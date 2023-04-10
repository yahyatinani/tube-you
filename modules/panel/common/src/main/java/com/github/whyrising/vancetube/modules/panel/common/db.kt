package com.github.whyrising.vancetube.modules.panel.common

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentVector
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

@Immutable
@Serializable
data class Suggestions(
  val query: String,
  @Stable
  val suggestions: PersistentVector<String>
)

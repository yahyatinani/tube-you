package com.github.whyrising.vancetube.modules.panel.home

import androidx.compose.runtime.Immutable
import com.github.whyrising.y.core.collections.IPersistentMap
import kotlinx.serialization.Serializable

typealias AppDb = IPersistentMap<Any, Any>

/**
 * Spec
 *
 * e.g:
 * {:state [States.Loading]
 * :popular_vids ([VideoData])}
 */

@Serializable
data class ThumbnailData(val url: String)

@Immutable
@Serializable
data class VideoData(
  val videoId: String,
  val title: String,
  val videoThumbnails: List<ThumbnailData>,
  val lengthSeconds: Int,
  val author: String,
  val authorId: String,
  val viewCount: Long,
  val publishedText: String
)

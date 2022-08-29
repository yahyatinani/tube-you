package com.github.whyrising.vancetube.home

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ThumbnailData(val url: String)

@Serializable
@Immutable
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

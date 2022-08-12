package com.github.whyrising.vancetube.home

import kotlinx.serialization.Serializable

@Serializable
data class Thumbnail(
  val url: String
)

@Serializable
data class VideoMetadata(
  val title: String,
  val videoThumbnails: List<Thumbnail>,
  val lengthSeconds: Int,
  val author: String,
  val authorId: String,
  val viewCount: String,
  val publishedText: String
)

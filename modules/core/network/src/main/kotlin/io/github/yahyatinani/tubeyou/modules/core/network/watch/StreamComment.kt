package io.github.yahyatinani.tubeyou.modules.core.network.watch

import kotlinx.serialization.Serializable

@Serializable
data class StreamComment(
  val author: String? = null,
  val thumbnail: String,
  val commentId: String,
  val commentText: String,
  val commentedTime: String,
  val commentorUrl: String? = null,
  val repliesPage: String? = null,
  val likeCount: Long,
  val replyCount: Long,
  val hearted: Boolean,
  val pinned: Boolean,
  val verified: Boolean
)

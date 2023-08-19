package io.github.yahyatinani.tubeyou.modules.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
) : Searchable

package io.github.yahyatinani.tubeyou.modules.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
) : Searchable

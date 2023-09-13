package io.github.yahyatinani.tubeyou.modules.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
) : Searchable

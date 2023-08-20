package io.github.yahyatinani.tubeyou.modules.core.network.watch

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class StreamDetails(
  val url: String,
  val format: String,
  val mimeType: String,
  val codec: String?,
  val quality: String,
  val videoOnly: Boolean,
  val contentLength: Long,
  val width: Int,
  val height: Int,
  val bitrate: Long,
  val initStart: Int? = null,
  val initEnd: Int? = null,
  val indexStart: Int? = null,
  val indexEnd: Int? = null,
  val fps: Int? = null,
  val audioTrackId: String? = null
)

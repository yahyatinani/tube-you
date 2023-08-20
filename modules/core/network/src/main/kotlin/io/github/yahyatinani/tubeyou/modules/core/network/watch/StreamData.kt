package io.github.yahyatinani.tubeyou.modules.core.network.watch

import androidx.compose.runtime.Immutable
import io.github.yahyatinani.tubeyou.modules.core.network.Searchable
import io.github.yahyatinani.y.core.v
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class StreamData(
  val title: String = "",
  val description: String = "",
  val uploadDate: String = "",
  val uploader: String = "",
  val uploaderUrl: String = "",
  val uploaderAvatar: String? = null,
  val thumbnailUrl: String = "",
  val hls: String? = null,
  val dash: String? = null,
  val category: String = "",
  val uploaderVerified: Boolean = false,
  val duration: Long = 0,
  val views: Long = 0,
  val likes: Long = 0,
  val dislikes: Long = 0,
  val uploaderSubscriberCount: Long = 0,
  val videoStreams: List<StreamDetails> = v(),
  val audioStreams: List<StreamDetails> = v(),
  val livestream: Boolean = false,
  val relatedStreams: List<Searchable> = v()
)

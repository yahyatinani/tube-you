package com.github.yahyatinani.tubeyou.modules.designsystem.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import io.github.yahyatinani.y.core.l

data class VideoViewModel(
  val id: String,
  val authorId: String,
  val title: String,
  val thumbnail: String,
  val length: String,
  val info: AnnotatedString,
  val isUpcoming: Boolean = false,
  val isShort: Boolean = false,
  val isLiveStream: Boolean = false,
  val uploaderAvatar: String? = null
)

@Immutable
data class Videos(val value: List<Any>) {
  companion object {
    private val videos: Videos = Videos(l())
    operator fun invoke(): Videos = videos
  }
}

data class ChannelVm(
  val id: String,
  val author: String,
  val handle: String,
  val subCount: String,
  val avatar: String
)

data class PlaylistVm(
  val title: String,
  val playlistId: String,
  val author: String,
  val authorId: String,
  val authorUrl: String,
  val videoCount: String,
  val thumbnailUrl: String
)

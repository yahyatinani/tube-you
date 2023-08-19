package io.github.yahyatinani.tubeyou.core.viewmodels

import androidx.compose.ui.text.AnnotatedString

data class VideoViewModel(
  val id: String = "",
  val authorId: String = "",
  val uploaderName: String = "",
  val title: String = "",
  val thumbnail: String = "",
  val length: String = "",
  val uploaded: String = "",
  val viewCount: String = "",
  val info: AnnotatedString = AnnotatedString(""),
  val isUpcoming: Boolean = false,
  val isShort: Boolean = false,
  val isLiveStream: Boolean = false,
  val uploaderAvatar: String? = null
)

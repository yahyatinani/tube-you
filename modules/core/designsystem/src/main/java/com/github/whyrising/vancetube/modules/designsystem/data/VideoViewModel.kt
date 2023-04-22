package com.github.whyrising.vancetube.modules.designsystem.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.github.whyrising.y.core.l

data class VideoViewModel(
  val id: String,
  val authorId: String,
  val title: String,
  val thumbnail: String,
  val length: String,
  val info: AnnotatedString,
  val isUpcoming: Boolean = false,
  val isShort: Boolean = false,
  val isLiveStream: Boolean = false
)

@Immutable
data class Videos(val value: List<VideoViewModel> = l())

data class ChannelVm(
  val id: String,
  val author: String,
  val handle: String,
  val subCount: String,
  val authorThumbnail: String
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

@Immutable
data class SearchVm(val value: List<Any> = l())

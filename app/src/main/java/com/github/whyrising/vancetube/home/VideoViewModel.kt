package com.github.whyrising.vancetube.home

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString

@Immutable
data class VideoViewModel(
  val id: String,
  val authorId: String,
  val title: String,
  val thumbnail: String,
  val length: String,
  val info: AnnotatedString
)

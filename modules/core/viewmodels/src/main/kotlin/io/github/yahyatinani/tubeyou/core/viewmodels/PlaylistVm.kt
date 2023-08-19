package io.github.yahyatinani.tubeyou.core.viewmodels

data class PlaylistVm(
  val title: String,
  val playlistId: String,
  val author: String,
  val authorUrl: String?,
  val videoCount: String,
  val thumbnailUrl: String
)

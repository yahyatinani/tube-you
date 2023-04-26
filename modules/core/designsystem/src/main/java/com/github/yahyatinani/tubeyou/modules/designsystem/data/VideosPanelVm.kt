package com.github.yahyatinani.tubeyou.modules.designsystem.data

data class VideosPanelVm(
  val isLoading: Boolean = false,
  val isRefreshing: Boolean = false,
  val showList: Boolean = false,
  val videos: Videos = default,
  val error: Int? = null
) {
  companion object {
    val default = Videos()
  }
}

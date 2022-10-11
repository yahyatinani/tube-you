package com.github.whyrising.vancetube.modules.designsystem.data

import com.github.whyrising.y.core.l

data class VideosPanelState(
  val isLoading: Boolean = false,
  val isRefreshing: Boolean = false,
  val showList: Boolean = false,
  val videos: Videos = default
) {
  companion object {
    val default = Videos(l())
  }
}

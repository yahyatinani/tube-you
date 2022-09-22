package com.github.whyrising.vancetube.home

import com.github.whyrising.y.core.l

enum class States {
  Loading,
  Refreshing,
  Loaded,
  Failed,
}

data class HomeDb(
  val state: States? = null,
  val popularVideos: List<VideoData> = l()
)

package com.github.whyrising.vancetube.home

import androidx.compose.runtime.Immutable

sealed interface Error

sealed interface HomePanelState {
  @JvmInline
  @Immutable
  value class Loaded(val popularVideos: List<VideoData>) : HomePanelState

  // TODO: Rework this by implementing subs cofx to get their values in event
  //  handlers
  @JvmInline
  @Immutable
  value class Refreshing(val currentPopularVideos: List<VideoViewModel>) :
    HomePanelState

  @JvmInline
  @Immutable
  value class Materialised(val popularVideos: List<VideoViewModel>) :
    HomePanelState

  object Loading : HomePanelState

  @JvmInline
  value class Failed(val error: Error) : HomePanelState
}

val HOME_START: HomePanelState = HomePanelState.Loading

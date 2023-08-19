package io.github.yahyatinani.tubeyou.core.viewmodels

sealed class PanelVm(
  open val videos: Videos = Videos(),
  val isLoading: Boolean = false,
  open val isAppending: Boolean = false,
  val isRefreshing: Boolean = false,
  open val error: Int? = null
) {
  object Loading : PanelVm(isLoading = true)

  data class Refreshing(override val videos: Videos) :
    PanelVm(videos = videos, isRefreshing = true)

  data class Loaded(
    override val videos: Videos = Videos(),
    override val isAppending: Boolean = false
  ) : PanelVm(videos = videos, isAppending = isAppending)

  data class Error(override val error: Int?) : PanelVm(error = error)

  companion object {
    val Init = Loaded()
  }
}

package io.github.yahyatinani.tubeyou.core.viewmodels

import androidx.compose.runtime.Immutable
import io.github.yahyatinani.y.core.l

@Immutable
data class Videos(val value: List<Any>) {
  companion object {
    private val videos: Videos = Videos(l())
    operator fun invoke(): Videos = videos
  }
}

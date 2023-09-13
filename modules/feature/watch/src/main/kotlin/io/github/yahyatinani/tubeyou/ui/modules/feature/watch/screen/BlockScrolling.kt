package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

object BlockScrolling : NestedScrollConnection {
  override fun onPostScroll(
    consumed: Offset,
    available: Offset,
    source: NestedScrollSource
  ): Offset = available
}

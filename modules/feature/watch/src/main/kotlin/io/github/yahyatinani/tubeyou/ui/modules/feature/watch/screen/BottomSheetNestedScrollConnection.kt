package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

internal class BottomSheetNestedScrollConnection : NestedScrollConnection {
  private var lasPos: NestedScrollSource? = null
  private var consume: Offset? = null
  override fun onPostScroll(
    consumed: Offset,
    available: Offset,
    source: NestedScrollSource
  ): Offset {
    if (source == NestedScrollSource.Fling) {
      lasPos = null
      consume = null
    }

    return if (consumed.x == 0f &&
      consumed.y == 0f &&
      lasPos == null
    ) {
      super.onPostScroll(consumed, available, source)
    } else {
      if (lasPos == null && source != NestedScrollSource.Fling) {
        lasPos = source
        consume = consumed
      }
      available
    }
  }

  override suspend fun onPostFling(
    consumed: Velocity,
    available: Velocity
  ): Velocity = available
}

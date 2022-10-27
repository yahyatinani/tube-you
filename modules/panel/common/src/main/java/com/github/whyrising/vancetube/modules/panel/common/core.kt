package com.github.whyrising.vancetube.modules.panel.common

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T> T.letIf(b: Boolean, block: (T) -> T): T {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return if (b) block(this) else this
}

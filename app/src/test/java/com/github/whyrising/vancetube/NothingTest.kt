package com.github.whyrising.vancetube

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.shouldBeExactly

class NothingTest : FreeSpec({
  "nothing" {
    1 + 1 shouldBeExactly 2
  }
})

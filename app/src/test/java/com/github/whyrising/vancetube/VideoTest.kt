package com.github.whyrising.vancetube

import com.github.whyrising.vancetube.home.formatSeconds
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class VideoTest : FreeSpec({
  "format seconds into HH:MM:SS for video length" {
    formatSeconds(551) shouldBe "9:11"
    formatSeconds(9) shouldBe "0:09"
    formatSeconds(66) shouldBe "1:06"
    formatSeconds(3600) shouldBe "1:00:00"
    formatSeconds(3954) shouldBe "1:05:54"
  }
})

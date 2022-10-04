package com.github.whyrising.vancetube.modules.panel.home

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

  "format views" {
    formatViews(0) shouldBe "0"
    formatViews(152) shouldBe "152"

    formatViews(1000) shouldBe "1K"
    formatViews(1249) shouldBe "1.2K"
    formatViews(7060) shouldBe "7K"
    formatViews(8091) shouldBe "8K"

    formatViews(10_000) shouldBe "10K"
    formatViews(16_403) shouldBe "16K"
    formatViews(29_088) shouldBe "29K"
    formatViews(14_344) shouldBe "14K"

    formatViews(162_757) shouldBe "162K"
    formatViews(871_363) shouldBe "871K"
    formatViews(999_999) shouldBe "999K"

    formatViews(1_000_000) shouldBe "1M"
    formatViews(58_933_685) shouldBe "58M"
    formatViews(283_303_265) shouldBe "283M"
    formatViews(999_999_999) shouldBe "999M"

    formatViews(3_015_415_459) shouldBe "3B"
    formatViews(11_133_682_004) shouldBe "11B"
  }
})

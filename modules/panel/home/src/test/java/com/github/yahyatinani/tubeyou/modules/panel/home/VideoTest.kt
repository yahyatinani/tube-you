package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatSeconds
import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatSubCount
import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatViews
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
    formatViews(1049) shouldBe "1K"
    formatViews(2078) shouldBe "2K"
    formatViews(1_178) shouldBe "1.1K"
    formatViews(1249) shouldBe "1.2K"
    formatViews(3854) shouldBe "3.8K"
    formatViews(7060) shouldBe "7K"
    formatViews(8091) shouldBe "8K"

    formatViews(10_000) shouldBe "10K"
    formatViews(15_859) shouldBe "15K"
    formatViews(30_900) shouldBe "30K"
    formatViews(16_403) shouldBe "16K"
    formatViews(29_088) shouldBe "29K"
    formatViews(14_344) shouldBe "14K"

    formatViews(162_757) shouldBe "162K"
    formatViews(871_363) shouldBe "871K"
    formatViews(999_999) shouldBe "999K"

    formatViews(1_000_000) shouldBe "1M"
    formatViews(6_058_456) shouldBe "6M"
    formatViews(5_016_439) shouldBe "5M"
    formatViews(9_906_500) shouldBe "9.9M"
    formatViews(9417535) shouldBe "9.4M"
    formatViews(8472896) shouldBe "8.4M"
    formatViews(58_933_685) shouldBe "58M"
    formatViews(283_303_265) shouldBe "283M"
    formatViews(999_999_999) shouldBe "999M"

    formatViews(1101440453) shouldBe "1.1B"
    formatViews(1290784643) shouldBe "1.2B"
    formatViews(3_015_415_459) shouldBe "3B"
    formatViews(11_133_682_004) shouldBe "11B"
  }

  "format sub count" {
    formatSubCount(12) shouldBe "12"
    formatSubCount(123) shouldBe "123"
    formatSubCount(1_234) shouldBe "1.2K"
    formatSubCount(12_345) shouldBe "12K"
    formatSubCount(123_456) shouldBe "123K"
    formatSubCount(1_234_567) shouldBe "1.2M"
    formatSubCount(12_345_678) shouldBe "12.3M"
    formatSubCount(14_100_000) shouldBe "14.1M"
    formatSubCount(29400000) shouldBe "29.4M"
    formatSubCount(123_456_789) shouldBe "123M"
    formatSubCount(1_234_567_890) shouldBe "1.2B"
    formatSubCount(1_023_456_789) shouldBe "1B"
  }
})

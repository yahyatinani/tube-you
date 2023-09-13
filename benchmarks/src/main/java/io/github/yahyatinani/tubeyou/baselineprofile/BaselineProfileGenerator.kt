package io.github.yahyatinani.tubeyou.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import io.github.yahyatinani.tubeyou.home.homeWaitForContent
import org.junit.Rule
import org.junit.Test

/**
 * Generates a baseline profile which can be copied to
 * `app/src/main/baseline-prof.txt`.
 */
class BaselineProfileGenerator {
  @get:Rule
  val baselineProfileRule = BaselineProfileRule()

  @Test
  fun generate() =
    baselineProfileRule.collect("io.github.yahyatinani.tubeyou.benchmark") {
      pressHome()
      startActivityAndWait()

      homeWaitForContent()
    }
}

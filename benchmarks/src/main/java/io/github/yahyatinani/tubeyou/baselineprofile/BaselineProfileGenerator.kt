package io.github.yahyatinani.tubeyou.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import io.github.yahyatinani.tubeyou.home.homeWaitForContent
import io.github.yahyatinani.tubeyou.watch.playVideo
import org.junit.Rule
import org.junit.Test

/**
 * Generates a baseline profile which can be copied to
 * `app/src/main/baseline-prof.txt`.
 */
class BaselineProfileGenerator {
  @get:Rule
  val baselineProfileRule = BaselineProfileRule()
  private val packageName = "io.github.yahyatinani.tubeyou.benchmark"

  @Test
  fun generate() = baselineProfileRule.collect(packageName) {
    pressHome()
    startActivityAndWait()

    homeWaitForContent()
/*    homeScrollDownUp()

    navigateToLibraryScreen()

    navigateToSubscriptionsScreen()

    // go back to Home screen.
    repeat(2) {
      device.pressBack()
      device.waitForIdle()
    }

    searchOpenSearchBar()
    val x = device.displayWidth / 2
    val y = device.displayHeight - (device.displayHeight / 4)
    searchPerformSearch(x = x, y = y)
    searchScrollDownUp()
    searchClear()
    searchPerformSearch(x = x + 200, y = y)

    // back Home.
    repeat(2) {
      device.pressBack()
      device.waitForIdle()
    }*/

    playVideo()
  }
}

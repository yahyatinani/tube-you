package io.github.yahyatinani.tubeyou.search

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren
import io.github.yahyatinani.tubeyou.waitAndFindObject

fun MacrobenchmarkScope.searchOpenSearchBar() {
  device.findObject(By.res("Top-bar search icon")).click()
  device.waitForIdle()
}

fun MacrobenchmarkScope.searchPerformSearch(x: Int, y: Int) {
  // emulate typing on the keyboard.
  device.click(x, y)

  val searchSuggestions =
    device.waitAndFindObject(By.res("search:suggestions_list"), 10_000)
  searchSuggestions.wait(untilHasChildren(), 60_000)

  device.pressEnter()
  device.waitForIdle()

  val searchResults =
    device.waitAndFindObject(By.res("search:results_list"), 10_000)
  searchResults.wait(untilHasChildren(), 60_000)
}

fun MacrobenchmarkScope.searchScrollDownUp() {
  val searchResults = device.findObject(By.res("search:results_list"))
  searchResults.fling(Direction.DOWN)
  device.wait(Until.gone(By.res("appendingIndicator")), 5_000)
  device.waitForIdle()
  searchResults.fling(Direction.UP)
  device.waitForIdle()
}

fun MacrobenchmarkScope.searchClear() {
  device.findObject(By.res("clear_search_input")).click()
  device.waitForIdle()
  device.waitAndFindObject(By.text("Search YouTube"), 10_000)
}

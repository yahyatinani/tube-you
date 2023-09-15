package io.github.yahyatinani.tubeyou.home

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren
import io.github.yahyatinani.tubeyou.waitAndFindObject

fun MacrobenchmarkScope.homeWaitForContent() {
  device.wait(Until.gone(By.res("loadingIndicator")), 5_000)
  val obj = device.waitAndFindObject(By.res("home:videos_list"), 10_000)
  obj.wait(untilHasChildren(), 60_000)
}

fun MacrobenchmarkScope.homeScrollDownUp() {
  val homeList = device.findObject(By.res("home:videos_list"))
  val margin = 10 * homeList.visibleBounds.width() / 100
  homeList.setGestureMargins(margin, margin, margin, margin)

  repeat(5) { homeList.scroll(Direction.DOWN, 1f, 100000) }
  repeat(5) { homeList.scroll(Direction.UP, 1f, 100000) }
}

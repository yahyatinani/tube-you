package io.github.yahyatinani.tubeyou.home

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.homeWaitForContent() {
  device.wait(Until.hasObject(By.text("Home")), 5_000)
}

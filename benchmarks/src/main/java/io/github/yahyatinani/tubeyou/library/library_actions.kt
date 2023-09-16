package io.github.yahyatinani.tubeyou.library

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.navigateToLibraryScreen() {
  device.findObject(By.text("Library")).click()
  device.waitForIdle()
  device.wait(
    Until.hasObject(By.text("todo: you not implement, yet.")),
    2_000
  )
}

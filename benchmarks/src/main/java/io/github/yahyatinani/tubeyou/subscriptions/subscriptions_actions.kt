package io.github.yahyatinani.tubeyou.subscriptions

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.navigateToSubscriptionsScreen() {
  device.findObject(By.text("Subscriptions")).click()
  device.waitForIdle()
  device.wait(
    Until.hasObject(By.text("todo: subscriptions not implement, yet.")),
    2_000
  )
}

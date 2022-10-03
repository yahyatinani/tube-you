package com.github.whyrising.vancetube.baselineprofile

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalBaselineProfilesApi
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
  @get:Rule
  val baselineProfileRule = BaselineProfileRule()

  @Test
  fun generate() = baselineProfileRule.collectBaselineProfile(
    packageName = "com.github.whyrising.vancetube"
  ) {
    pressHome()
    startActivityAndWait()

    val listTag = "popular_videos_list"
    device.wait(Until.hasObject(By.res(listTag)), 5_000)
    val contentList = device.findObject(By.res(listTag))
    contentList.wait(Until.hasObject(By.res("video")), 5000)

    contentList.fling(Direction.DOWN)
    device.waitForIdle()
    contentList.fling(Direction.UP)
    device.waitForIdle()

    val swipeRefresh = device.findObject(By.res("swipe_refresh"))
    swipeRefresh.swipe(Direction.DOWN, .8f)
    device.wait(Until.gone(By.res("swipe_refresh")), 5000)

    // Navigate to Subscriptions tab
    device.findObject(By.text("Subscriptions")).click()
    device.waitForIdle()

    // Navigate to Library tab
    device.findObject(By.text("Library")).click()
    device.waitForIdle()

    // Navigate back to Home tab
    device.findObject(By.text("Home")).click()
  }
}

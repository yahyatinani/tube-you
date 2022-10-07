package com.github.whyrising.vancetube.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileBenchmark {
  @get:Rule
  val benchmarkRule = MacrobenchmarkRule()

  private fun startup(compilationMode: CompilationMode) {
    benchmarkRule.measureRepeated(
      packageName = "com.github.whyrising.vancetube",
      metrics = listOf(StartupTimingMetric()),
      iterations = 10,
      startupMode = StartupMode.COLD,
      compilationMode = compilationMode,
      setupBlock = {
        pressHome()
      }
    ) {
      startActivityAndWait()
    }
  }

  @Test
  fun startupNoCompilation() {
    startup(CompilationMode.None())
  }

  @Test
  fun startupBaselineProfile() {
    startup(CompilationMode.Partial(BaselineProfileMode.Require))
  }

  @Test
  fun navigateBottomBar() {
    val listTag = "popular_videos_list"
    benchmarkRule.measureRepeated(
      packageName = "com.github.whyrising.vancetube",
      metrics = listOf(FrameTimingMetric()),
      iterations = 10,
      startupMode = StartupMode.COLD,
      compilationMode = CompilationMode.Partial(BaselineProfileMode.Require),
      setupBlock = {
        pressHome()
        startActivityAndWait()
        device.wait(Until.hasObject(By.res(listTag)), 20_000)
        val contentList = device.findObject(By.res(listTag))
        contentList.wait(Until.hasObject(By.res("video")), 5000)

        // Navigate to Library tab
        device.findObject(By.text("Library")).click()
        device.waitForIdle()
        device.wait(Until.hasObject(By.res("sdfsdf")), 2_000)
      }
    ) {
      // Navigate back to Home tab
      device.findObject(By.text("Home")).click()
      device.wait(Until.hasObject(By.res(listTag)), 20_000)
    }
  }
}

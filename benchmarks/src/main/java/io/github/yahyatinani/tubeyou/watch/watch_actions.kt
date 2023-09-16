package io.github.yahyatinani.tubeyou.watch

import android.graphics.Point
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren
import io.github.yahyatinani.tubeyou.flingElementDownUp
import io.github.yahyatinani.tubeyou.waitAndFindObject

fun MacrobenchmarkScope.playVideo() {
  device.click(device.displayWidth / 2, device.displayHeight / 2)
  device.wait(Until.gone(By.res("watch:content_loader")), 5_000)
  device.wait(Until.gone(By.res("watch:comments_section_loader")), 5_000)

  val relatedList =
    device.waitAndFindObject(By.res("watch:related_streams"), 10_000)
  relatedList.wait(untilHasChildren(), 60_000)

  device.waitAndFindObject(By.res("watch:comments_section"), 10_000).click()

  val comments = device.waitAndFindObject(By.res("watch:comments_list"), 10_000)
  comments.wait(untilHasChildren(), 60_000)

//  device.flingElementDownUp(comments)

  comments.findObject(By.textContains("replies")).click()
//  comments.children[0].click()
//  device.waitAndFindObject(By.res("watch:comment"), 10_000).click()

  val replies = device.waitAndFindObject(By.res("watch:replies_list"), 10_000)
  replies.wait(untilHasChildren(), 60_000)
  device.flingElementDownUp(replies)
  device.waitForIdle()

  device.pressBack()
  device.wait(Until.gone(By.res("watch:replies_list")), 5_000)

  device.pressBack()
  device.wait(Until.gone(By.res("watch:comments_list")), 5_000)

  device.waitAndFindObject(By.res("watch:description_section"), 10_000).click()
  val desc = device.waitAndFindObject(By.res("watch:description_list"), 10_000)
  device.flingElementDownUp(desc)
  device.waitForIdle()

//  device.pressBack()
  device.waitAndFindObject(By.res("watch:description_top_bar"), 10_000)
    .drag(Point(0, device.displayWidth))
//  device.wait(Until.gone(By.res("watch:description_list")), 5_000)

//  device.pressBack()
  device.waitForIdle()

//  device.waitAndFindObject(By.res("watch:mini_player"), 10_000).click()
//  device.wait(Until.gone(By.res("watch:mini_player_controls")), 5_000)
//  device.pressBack()
//  device.waitAndFindObject(By.res("watch:mini_player_controls"), 10_000)

  device.waitAndFindObject(By.res("watch:video_player"), 10_000)
    .drag(Point(0, device.displayWidth))

  device.waitAndFindObject(By.res("watch:close_mini_player_btn"), 10_000)
    .click()
}

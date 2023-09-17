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

  val relatedList =
    device.waitAndFindObject(By.res("watch:related_streams"), 10_000)
  relatedList.wait(untilHasChildren(), 60_000)

  device.wait(Until.gone(By.res("watch:content_loader")), 5_000)
  device.wait(Until.gone(By.res("watch:comments_section_loader")), 5_000)

  device.waitForIdle()

  device.waitAndFindObject(By.res("watch:comments_section"), 10_000).click()

  val comments = device.waitAndFindObject(By.res("watch:comments_list"), 10_000)
  comments.wait(untilHasChildren(), 60_000)

//  device.flingElementDownUp(comments)

  val commentReplies = comments.findObject(By.textContains("replies"))
  if (commentReplies != null) {
    commentReplies.click()
    val replies = device.waitAndFindObject(By.res("watch:replies_list"), 10_000)
    replies.wait(untilHasChildren(), 60_000)
    device.flingElementDownUp(replies)
    device.waitForIdle()

    device.pressBack()
    device.wait(Until.gone(By.res("watch:replies_list")), 5_000)
  }

  device.waitAndFindObject(By.res("watch:close_comments_desc_sheet"), 10_000)
    .click()

  device.waitAndFindObject(By.res("watch:description_section"), 10_000).click()
  val desc = device.waitAndFindObject(By.res("watch:description_list"), 10_000)
//  device.flingElementDownUp(desc)

  device.waitAndFindObject(By.res("watch:close_comments_desc_sheet"), 10_000)
    .click()

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

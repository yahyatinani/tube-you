package com.github.whyrising.vancetube

import android.app.Application
import com.github.whyrising.vancetube.base.db.initAppDb

class App : Application() {
  override fun onCreate() {
    super.onCreate()

    initAppDb()

    // FIXME: remove this for release
//    System.setProperty("kotlinx.coroutines.debug", "on")
//    Log.i("currentThreadName", Thread.currentThread().name)
  }
}

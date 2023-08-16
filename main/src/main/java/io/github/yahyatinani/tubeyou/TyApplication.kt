package io.github.yahyatinani.tubeyou

import android.app.Application

class TyApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    // FIXME: remove this for release
//    System.setProperty("kotlinx.coroutines.debug", "on")
//    Log.i("currentThreadName", Thread.currentThread().name)
  }
}

package io.github.yahyatinani.tubeyou

import android.app.Application
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.httpfx.httpFxClient
import io.github.yahyatinani.recompose.regEventDb
import io.github.yahyatinani.tubeyou.db.defaultAppState
import io.github.yahyatinani.tubeyou.modules.core.network.tyHttpClient
import io.github.yahyatinani.y.core.v

class TyApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    regEventDb<Any>(common.init_app_db) { _, _ -> defaultAppState }
    dispatchSync(v(common.init_app_db))

    httpFxClient = tyHttpClient

    // FIXME: remove this for release
//    System.setProperty("kotlinx.coroutines.debug", "on")
//    Log.i("currentThreadName", Thread.currentThread().name)
  }
}

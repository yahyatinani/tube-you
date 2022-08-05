package com.github.whyrising.composetemplate

import android.app.Application
import com.github.whyrising.composetemplate.base.base
import com.github.whyrising.composetemplate.base.base.init_db
import com.github.whyrising.composetemplate.home.home
import com.github.whyrising.recompose.dispatchSync
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.v

fun initAppDb() {
  regEventDb<IPersistentMap<Any, Any>>(init_db) { db, _ ->
    db.assoc(base.is_backstack_available, false)
      .assoc(home.count, 0)
  }
  dispatchSync(v(init_db))
}

class App : Application() {
  override fun onCreate() {
    super.onCreate()

    // TODO: init
    initAppDb()
  }
}

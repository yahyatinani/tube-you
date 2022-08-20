package com.github.whyrising.vancetube

import android.app.Application
import com.github.whyrising.recompose.dispatchSync
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.vancetube.base.base.init_db
import com.github.whyrising.vancetube.home.VideoMetadata
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

const val https = "https://"

//const val DEFAULT_BASE_ADDRESS = "invidious.tiekoetter.com"
//const val DEFAULT_BASE_ADDRESS = "invidious.namazso.eu" // empty
const val DEFAULT_BASE_ADDRESS = "y.com.sb"

const val API = "$https$DEFAULT_BASE_ADDRESS/api/v1"

fun initAppDb() {
  regEventDb<IPersistentMap<Any, Any>>(init_db) { _, _ ->
    m(
      base.is_backstack_available to false,
      base.api to API,
      home.panel to m(
        home.popular_vids to v<VideoMetadata>(),
        home.is_loading to true,
        home.is_refreshing to false
      )
    )
  }
  dispatchSync(v(init_db))
}

class App : Application() {
  override fun onCreate() {
    super.onCreate()

    initAppDb()
  }
}

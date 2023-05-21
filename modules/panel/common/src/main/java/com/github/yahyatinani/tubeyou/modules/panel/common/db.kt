package com.github.yahyatinani.tubeyou.modules.panel.common

import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.yahyatinani.tubeyou.modules.core.keywords.common

typealias AppDb = IPersistentMap<Any, Any>

fun activeTab(appDb: AppDb): Any = appDb[common.active_navigation_item]!!

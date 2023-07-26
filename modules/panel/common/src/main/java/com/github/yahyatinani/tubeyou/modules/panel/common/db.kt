package com.github.yahyatinani.tubeyou.modules.panel.common

import androidx.compose.runtime.Immutable
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.cofx.Coeffects
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m

typealias AppDb = IPersistentMap<Any, Any>

fun activeTab(appDb: AppDb): Any = appDb[common.active_navigation_item]!!

fun appDbBy(cofx: Coeffects): AppDb = cofx[recompose.db] as AppDb

@Immutable
data class UIState(val data: Any = m<Any, Any>())

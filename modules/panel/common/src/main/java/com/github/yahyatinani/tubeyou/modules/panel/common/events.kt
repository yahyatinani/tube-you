package com.github.yahyatinani.tubeyou.modules.panel.common

import com.github.whyrising.recompose.cofx.Coeffects
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.y.core.get

enum class States { Loading, Refreshing, Loaded, Failed }

fun appDbBy(cofx: Coeffects): AppDb = cofx[db] as AppDb

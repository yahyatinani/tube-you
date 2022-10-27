package com.github.whyrising.vancetube.modules.panel.common

import com.github.whyrising.recompose.cofx.Coeffects
import com.github.whyrising.recompose.ids.recompose
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l

enum class States { Loading, Refreshing, Loaded, Failed }

fun nextState(
  fsm: Map<Any?, Any>,
  currentState: States?,
  transition: Any
): Any? = getIn(fsm, l(currentState, transition))

fun appDbBy(cofx: Coeffects): AppDb = cofx[recompose.db] as AppDb

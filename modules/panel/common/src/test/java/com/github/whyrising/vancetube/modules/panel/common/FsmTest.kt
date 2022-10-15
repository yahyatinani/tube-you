package com.github.whyrising.vancetube.modules.panel.common

import com.github.whyrising.vancetube.modules.core.keywords.common.initialize
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.load
import com.github.whyrising.vancetube.modules.core.keywords.home.set_popular_vids
import com.github.whyrising.vancetube.modules.panel.common.States.Failed
import com.github.whyrising.vancetube.modules.panel.common.States.Loaded
import com.github.whyrising.vancetube.modules.panel.common.States.Loading
import com.github.whyrising.vancetube.modules.panel.common.States.Refreshing
import com.github.whyrising.y.core.m
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FsmTest : FreeSpec({
  "nextState() should return next state" {
    val fsm = m<Any?, Any>(
      null to m(initialize to Loading),
      Loading to m(
        set_popular_vids to Loaded,
        ":error" to Failed
      ),
      Loaded to m(
        home.refresh to Refreshing,
        load to Loaded
      ),
      Refreshing to m(
        set_popular_vids to Loaded,
        ":error" to Failed
      ),
      Failed to m(load to Loading)
    )

    nextState(fsm, null, initialize) shouldBe Loading
    nextState(fsm, Loading, set_popular_vids) shouldBe Loaded
    nextState(fsm, Loaded, home.refresh) shouldBe Refreshing
    nextState(fsm, Refreshing, set_popular_vids) shouldBe Loaded
    nextState(fsm, Loaded, load) shouldBe Loaded
  }
})

package com.github.yahyatinani.tubeyou.modules.panel.common

import com.github.whyrising.y.core.m
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.initialize
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.error
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.load
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.loading_is_done
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.refresh
import com.github.yahyatinani.tubeyou.modules.panel.common.States.Failed
import com.github.yahyatinani.tubeyou.modules.panel.common.States.Loaded
import com.github.yahyatinani.tubeyou.modules.panel.common.States.Loading
import com.github.yahyatinani.tubeyou.modules.panel.common.States.Refreshing
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FsmTest : FreeSpec({
  "nextState() should return next state" {
    val fsm = m<Any?, Any>(
      null to m(initialize to Loading),
      Loading to m(
        loading_is_done to Loaded,
        error to Failed
      ),
      Loaded to m(
        refresh to Refreshing,
        load to Loaded
      ),
      Refreshing to m(
        loading_is_done to Loaded,
        error to Failed
      ),
      Failed to m(load to Loading)
    )

    nextState(fsm, null, initialize) shouldBe Loading
    nextState(fsm, Loading, loading_is_done) shouldBe Loaded
    nextState(fsm, Loaded, refresh) shouldBe Refreshing
    nextState(fsm, Refreshing, loading_is_done) shouldBe Loaded
    nextState(fsm, Loaded, load) shouldBe Loaded
  }
})

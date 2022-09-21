package com.github.whyrising.vancetube.home

import com.github.whyrising.vancetube.base.db.initialDb
import com.github.whyrising.vancetube.home.States.Loaded
import com.github.whyrising.vancetube.home.States.Loading
import com.github.whyrising.vancetube.home.States.Refreshing
import com.github.whyrising.vancetube.home.home.load_popular_videos
import com.github.whyrising.vancetube.home.home.refresh
import com.github.whyrising.vancetube.home.home.set_popular_vids
import com.github.whyrising.y.core.v
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FsmTest : FreeSpec({
  "nextState() should return next state" {
    nextState(homeStateMachine, null, load_popular_videos) shouldBe Loading
    nextState(homeStateMachine, Loading, set_popular_vids) shouldBe Loaded
    nextState(homeStateMachine, Loaded, refresh) shouldBe Refreshing
    nextState(homeStateMachine, Refreshing, set_popular_vids) shouldBe Loaded
    nextState(homeStateMachine, Loaded, load_popular_videos) shouldBe Loaded
  }

  "updateToNextState() should update AppDb to next state" {
    updateToNextState(initialDb, load_popular_videos) shouldBe initialDb

    updateToNextState(initialDb, set_popular_vids) shouldBe initialDb.assoc(
      home.panel,
      HomeDb(Loaded)
    )
  }

  "handleNextState() should update AppDb to next state" {
    handleNextState(initialDb, v(load_popular_videos)) shouldBe initialDb

    handleNextState(initialDb, v(set_popular_vids)) shouldBe initialDb.assoc(
      home.panel,
      HomeDb(Loaded)
    )
  }
})

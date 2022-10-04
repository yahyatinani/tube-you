package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.load_popular_videos
import com.github.whyrising.vancetube.modules.core.keywords.home.set_popular_vids
import com.github.whyrising.vancetube.modules.panel.home.States.Loaded
import com.github.whyrising.vancetube.modules.panel.home.States.Loading
import com.github.whyrising.vancetube.modules.panel.home.States.Refreshing
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FsmTest : FreeSpec({
  "nextState() should return next state" {
    nextState(
      homeStateMachine,
      null,
      load_popular_videos
    ) shouldBe Loading
    nextState(
      homeStateMachine,
      Loading,
      set_popular_vids
    ) shouldBe Loaded
    nextState(
      homeStateMachine,
      Loaded,
      home.refresh
    ) shouldBe Refreshing
    nextState(
      homeStateMachine,
      Refreshing,
      set_popular_vids
    ) shouldBe Loaded
    nextState(
      homeStateMachine,
      Loaded,
      load_popular_videos
    ) shouldBe Loaded
  }

  "updateToNextState() should update AppDb to next state" {
    updateToNextState(
      m(),
      load_popular_videos
    ) shouldBe
      assocIn(
        m<Any, Any>(),
        l(
          home.panel,
          home.state
        ),
        Loading
      )

    updateToNextState(
      assocIn(
        m<Any, Any>(),
        l(
          home.panel,
          home.state
        ),
        Loading
      ) as AppDb,
      set_popular_vids
    ) shouldBe assocIn(
      m<Any, Any>(),
      l(
        home.panel,
        home.state
      ),
      Loaded
    )
  }

  "handleNextState() should update AppDb to next state" {
    handleNextState(
      m(),
      v(load_popular_videos)
    ) shouldBe
      assocIn(
        m<Any, Any>(),
        l(
          home.panel,
          home.state
        ),
        Loading
      )

    handleNextState(
      m(home.panel to m(home.state to Loaded)),
      v(set_popular_vids)
    ) shouldBe assocIn(
      m<Any, Any>(),
      l(home.panel, home.state),
      Loaded
    )
  }
})

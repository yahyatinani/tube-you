package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.vancetube.modules.core.keywords.common.initialize
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.set_popular_vids
import com.github.whyrising.vancetube.modules.panel.common.AppDb
import com.github.whyrising.vancetube.modules.panel.common.States.Loaded
import com.github.whyrising.vancetube.modules.panel.common.States.Loading
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class FsmTest : FreeSpec({
  "updateToNextState() should update AppDb to next state" {
    updateToNextState(m(), initialize) shouldBe
      assocIn(m<Any, Any>(), l(home.panel, home.state), Loading)

    updateToNextState(
      db = assocIn(m(), l(home.panel, home.state), Loading) as AppDb,
      event = set_popular_vids
    ) shouldBe assocIn(m<Any, Any>(), l(home.panel, home.state), Loaded)
  }

  "handleNextState() should update AppDb to next state" {
    handleNextState(m(), v(initialize)) shouldBe
      assocIn(m<Any, Any>(), l(home.panel, home.state), Loading)

    handleNextState(
      db = m(home.panel to m(home.state to Loaded)),
      v(set_popular_vids)
    ) shouldBe assocIn(m<Any, Any>(), l(home.panel, home.state), Loaded)
  }
})

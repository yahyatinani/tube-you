package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.fx.BuiltInFx
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.loading_is_done
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
  val map = m<Any, Any>()

  "updateToNextState() should update AppDb to next state" {
    updateToNextState(m(), home.initialize) shouldBe assocIn(
      map,
      l(home.panel, home.state),
      v(Loading, v(v(BuiltInFx.dispatch, v(home.load))))
    )

    updateToNextState(
      db = assocIn(m(), l(home.panel, home.state), v(Loading)) as AppDb,
      event = loading_is_done
    ) shouldBe assocIn(map, l(home.panel, home.state), v(Loaded))
  }

  "handleNextState() should update AppDb to next state" {
    handleNextState(m(), v(home.initialize)) shouldBe assocIn(
      map,
      l(home.panel, home.state),
      v(Loading, v(v(BuiltInFx.dispatch, v(home.load))))
    )

    handleNextState(
      db = m(home.panel to m(home.state to Loaded)),
      v(loading_is_done)
    ) shouldBe assocIn(map, l(home.panel, home.state), Loaded)
  }
})

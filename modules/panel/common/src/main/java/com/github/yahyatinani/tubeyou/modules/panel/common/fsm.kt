package com.github.yahyatinani.tubeyou.modules.panel.common

import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.BuiltInFx
import com.github.whyrising.recompose.fx.Effects
import com.github.whyrising.recompose.ids.recompose
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.IPersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import kotlin.reflect.KFunction3

// TODO: Move this file to re-compose.

typealias State = IPersistentMap<Any, Any?>?

@Suppress("EnumEntryName", "ClassName")
enum class fsm { _state, guard, target, actions, ALL }

internal fun fsmMap(
  next: Any,
  appDb: AppDb,
  stateMap: State,
  event: Event
): IPersistentMap<Any, Any?> = when (next) {
  is IPersistentVector<*> -> { // guard
    val m1 = next[0] as IPersistentMap<Any, Any?>
    val guard = m1[fsm.guard] as KFunction3<AppDb, State, Event, Boolean>
    if (guard(appDb, stateMap, event)) m1
    else fsmMap(next[1] as Any, appDb, stateMap, event)
  }

  else -> next
} as IPersistentMap<Any, Any?>

fun trigger(
  machine: IPersistentMap<Any?, Any?>,
  fxs: Effects,
  statePath: IPersistentVector<Any>,
  event: Event
): Effects {
  val appDb = fxs[recompose.db] as AppDb
  val stateMap = getIn<State>(appDb, statePath.seq())
  val currentState = stateMap?.get(fsm._state)
  val eventId = event[0]
  val next = getIn<Any>(machine, l(currentState, eventId))
    ?: getIn<Any>(machine, l(fsm.ALL, eventId))
    ?: return fxs

  val next2 = fsmMap(next, appDb, stateMap, event)
  val _nextState = when (val targetState = next2[fsm.target]) {
    fsm.ALL -> currentState // keep same state.
    else -> targetState
  }
  val nextState = if (_nextState != null) {
    stateMap?.assoc(fsm._state, _nextState) ?: m(fsm._state to _nextState)
  } else stateMap?.dissoc(fsm._state)

  val actions = when (val action = next2[fsm.actions]) {
    is KFunction3<*, *, *, *> -> v(action)
    else -> action
  } as IPersistentVector<KFunction3<AppDb, State, Event, Effects>>?

  val initial =
    fxs.assoc(recompose.db, assocIn(appDb, statePath.seq(), nextState))

  return actions?.fold(initial) { effects: Effects, action ->
    val effectsDb = effects[recompose.db] as AppDb
    val effectsState = getIn<State>(effectsDb, statePath.seq())

    val r = action(effectsDb, effectsState, event)

    val newDb = r[recompose.db] as AppDb? ?: effectsDb
    val _state = r[fsm._state] as State ?: effectsState
    val moreFxs = r[BuiltInFx.fx] as IPersistentVector<Any>?
    val newAppDb = if (_nextState != null) {
      assocIn(newDb, statePath.seq(), _state)
    } else { // fsm halt aka null => remove state from db.
      val last = statePath.peek()!!
      val butLast = statePath.pop().seq()
      val tmp = getIn<IPersistentMap<Any, Any>>(newDb, butLast)?.dissoc(last)
      assocIn(newDb, butLast, tmp)
    }

    effects.assoc(recompose.db, newAppDb).assoc(fsm._state, _state)
      .let { oldFx ->
        if (moreFxs == null) return@let oldFx

        val f = moreFxs.fold(
          (oldFx[BuiltInFx.fx] as IPersistentVector<Any>?) ?: v()
        ) { acc, newFx -> acc.conj(newFx) }
        oldFx.assoc(BuiltInFx.fx, f)
      }
  } ?: initial
}

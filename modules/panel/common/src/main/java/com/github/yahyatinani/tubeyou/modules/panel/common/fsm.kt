package com.github.yahyatinani.tubeyou.modules.panel.common

import com.github.yahyatinani.tubeyou.modules.panel.common.fsm.parallel
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm.regions
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm.type
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.y.core.assocIn
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.collections.IPersistentVector
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import kotlin.reflect.KFunction3

// TODO: Move this file to re-compose.

typealias State = IPersistentMap<Any, Any?>
typealias Machine = IPersistentMap<Any?, Any?>
typealias Action = KFunction3<AppDb, State?, Event, Effects>
typealias Guard = KFunction3<AppDb, State?, Event, Boolean>

@Suppress("EnumEntryName", "ClassName")
enum class fsm {
  _state, guard, target, actions, ALL, state_map, type, parallel, regions
}

fun transitionMap(
  transition: Any,
  appDb: AppDb,
  stateMap: State?,
  event: Event
): IPersistentMap<Any, Any?> = when (transition) {
  is IPersistentVector<*> -> { // guard
    val m1 = transition[0] as IPersistentMap<Any, Any?>
    val guard = m1[fsm.guard]
    val flag: Boolean = if (guard is IPersistentVector<*>) {
      (guard as IPersistentVector<Guard>).fold(true) { acc, predicate ->
        acc && predicate(appDb, stateMap, event)
      }
    } else (guard as Guard)(appDb, stateMap, event)
    if (flag) m1
    else transitionMap(transition[1] as Any, appDb, stateMap, event)
  }

  else -> transition
} as IPersistentMap<Any, Any?>

@Suppress("UNCHECKED_CAST", "FunctionName")
internal fun _trigger(
  machine: IPersistentMap<Any?, Any?>,
  appDb: AppDb,
  stateMap: State?,
  event: Event
): Effects? {
  val currentState = stateMap?.get(fsm._state)
  val eventId = event[0]
  val transition = getIn<Any>(machine, l(currentState, eventId))
    ?: getIn<Any>(machine, l(fsm.ALL, eventId))
    ?: return null

  val transitionMap = transitionMap(transition, appDb, stateMap, event)

  val nextState = when (val targetState = transitionMap[fsm.target]) {
    fsm.ALL -> currentState // keep same state.
    else -> targetState
  }

  val nextStateMap: State? = when (nextState) {
    null -> stateMap?.dissoc(fsm._state)
    else -> {
      stateMap?.assoc(fsm._state, nextState) ?: m(fsm._state to nextState)
    }
  }
  val actions = when (val action = transitionMap[fsm.actions]) {
    is KFunction3<*, *, *, *> -> v(action)
    else -> action
  } as IPersistentVector<KFunction3<AppDb, State?, Event, Effects>>?
  val initial =
    if (nextStateMap != null) m(fsm.state_map to nextStateMap) else m()
  return actions?.fold(initial) { effects: Effects, action ->
    val effectsState = get<State>(effects, fsm.state_map)
    val actionsFxs = action(appDb, effectsState, event)

    val actionsState: State? =
      actionsFxs[fsm.state_map] as State? ?: effectsState
    val moreFxs = actionsFxs[BuiltInFx.fx] as IPersistentVector<Any>?
    effects.assoc(fsm.state_map, actionsState)
      .let { oldFx ->
        if (moreFxs == null) return@let oldFx

        val f = moreFxs.fold(
          (oldFx[BuiltInFx.fx] as IPersistentVector<Any>?) ?: v()
        ) { acc, newFx -> acc.conj(newFx) }
        oldFx.assoc(BuiltInFx.fx, f)
      }
  } ?: initial
}

internal fun triggerParallel(
  machine: Machine,
  appDb: AppDb,
  stateMap: State?,
  event: Event
): Effects? {
  val regions = machine[regions] as IPersistentVector<IPersistentVector<Any>>
  val initial = m(fsm.state_map to stateMap)
  val effectsWithActions = regions.fold(
    initial = initial
  ) { accFx: Effects, (fsmKey, fsmMachine) ->
    fsmMachine as Machine
    val currentState = stateMap?.get(fsm._state) as State?
    val regionCurrentState = currentState?.get(fsmKey)
    val eventId = event[0]
    val rTransition = getIn<Any>(fsmMachine, l(regionCurrentState, eventId))
      ?: getIn<Any>(fsmMachine, l(fsm.ALL, eventId))
      ?: return@fold accFx

    val rTransitionMap = transitionMap(rTransition, appDb, stateMap, event)

    val nextRegionState =
      when (val targetState = rTransitionMap[fsm.target]) {
        fsm.ALL -> regionCurrentState // keep same state.
        else -> targetState
      }

    val newFx = if (nextRegionState == null) {
      val _tmpState =
        getIn<State>(accFx, l(fsm.state_map, fsm._state))?.dissoc(fsmKey)
      assocIn(accFx, l(fsm.state_map, fsm._state), _tmpState)
    } else {
      assocIn(accFx, l(fsm.state_map, fsm._state, fsmKey), nextRegionState)
    }

    val accActions = get<IPersistentVector<Any>>(accFx, fsm.actions) ?: v()
    val rActions = when (val action = rTransitionMap[fsm.actions]) {
      is KFunction3<*, *, *, *> -> v(action)
      else -> action
    } as IPersistentVector<Action>?
    val mergeActions = rActions?.fold(accActions) { acc, actionFn ->
      acc.conj(actionFn)
    } ?: accActions

    when {
      mergeActions.isEmpty() -> newFx
      else -> newFx.assoc(fsm.actions, mergeActions)
    } as Effects
  }

  if (effectsWithActions === initial) return null

  val actions = effectsWithActions[fsm.actions] as IPersistentVector<Action>?
  val nextStateMap: State = (effectsWithActions[fsm.state_map] as State).let {
    val _state = it[fsm._state] as State?
    if (_state != null && _state.count == 0) it.assoc(fsm._state, null) else it
  }

  val initialEffects: Effects = m(fsm.state_map to nextStateMap)
  return actions?.fold(initialEffects) { accFx: Effects, action: Action ->
    val effectsState = get<State>(accFx, fsm.state_map)
    val actionsFxs = action(appDb, effectsState, event)

    val actionsState: State? =
      actionsFxs[fsm.state_map] as State? ?: effectsState
    val moreFxs = actionsFxs[BuiltInFx.fx] as IPersistentVector<Any>?
    accFx.assoc(fsm.state_map, actionsState)
      .let { oldFx ->
        if (moreFxs == null) return@let oldFx

        val f = moreFxs.fold(
          (oldFx[BuiltInFx.fx] as IPersistentVector<Any>?) ?: v()
        ) { acc, newFx -> acc.conj(newFx) }
        oldFx.assoc(BuiltInFx.fx, f)
      }
  } ?: initialEffects
}

fun trigger(
  machine: Machine,
  fxs: Effects,
  statePath: IPersistentVector<Any>,
  event: Event
): Effects {
  val appDb = fxs[recompose.db] as AppDb
  val stateMap = getIn<State>(appDb, statePath.seq())

  val isParallel = machine[type] == parallel
  val effects: Effects = when {
    isParallel -> triggerParallel(machine, appDb, stateMap, event)
    else -> _trigger(machine, appDb, stateMap, event)
  } ?: return fxs

  val nextStateMap = effects[fsm.state_map] as State?
  val nextState = nextStateMap?.get(fsm._state)
  val newAppDb = if (nextState != null) {
    assocIn(appDb, statePath.seq(), nextStateMap)
  } else { // fsm halt aka null => remove state from db.
    val last = statePath.peek()!!
    val butLast = statePath.pop().seq()
    val tmp = getIn<IPersistentMap<Any, Any>>(appDb, butLast)?.dissoc(last)
    assocIn(appDb, butLast, tmp)
  }

  return effects.assoc(recompose.db, newAppDb)
}

enum class PanelStates { LOADING, REFRESHING, LOADED, FAILED }

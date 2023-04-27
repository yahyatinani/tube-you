package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class home {
  state,
  view_model,
  load,
  go_top_list,
  fsm_next_state,
  coroutine_scope,
  fsm_state,

  // FSM events
  initialize,
  loading_is_done,
  refresh,
  error;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

const val HOME_GRAPH_ROUTE = "home_graph"
const val HOME_ROUTE = "home_route"

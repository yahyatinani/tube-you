package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class home {
  view_model,
  load_trending,
  go_top_list,
  coroutine_scope,
  fsm_state,
  fsm,
  content,

  // FSM events
  load,
  refresh,
  set_loading_results,
  set_loading_error;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

const val HOME_GRAPH_ROUTE = "home_graph"
const val HOME_ROUTE = "home_route"

package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class home {
  state,
  view_model,
  load_trending,
  go_top_list,
  coroutine_scope,
  fsm_state,

  // FSM events
  load,
  loading_is_done,
  refresh,
  loading_failed;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

const val HOME_GRAPH_ROUTE = "home_graph"
const val HOME_ROUTE = "home_route"

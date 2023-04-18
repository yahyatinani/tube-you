package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class home {
  state,
  view_model,
  refresh,
  load,
  go_top_list,
  popular_vids,
  error,
  initialize,
  loading_is_done,
  fsm,
  coroutine_scope,
  db;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

const val HOME_GRAPH_ROUTE = "home_graph"
const val HOME_ROUTE = "home_route"

package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class home {
  route,
  panel,
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
  coroutine_scope;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

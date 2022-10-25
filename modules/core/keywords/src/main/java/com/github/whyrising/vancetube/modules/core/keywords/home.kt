package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class home {
  route,
  panel,
  state,
  view_model,
  set_popular_vids,
  refresh,
  load,
  go_top_list,
  popular_vids,
  error,
  fsm,
  coroutine_scope;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

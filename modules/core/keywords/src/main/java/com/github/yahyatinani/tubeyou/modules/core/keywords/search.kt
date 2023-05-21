package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class search {
  // FSM events:
  show_search_bar,
  update_search_input,
  back_press_search,
  submit,
  set_search_results,
  search_failed,
  clear_search_input,

  route,
  stack,
  state,
  get_search_results,
  view_model,
  sb_fsm,
  get_search_suggestions,
  set_suggestions,
  coroutine_scope;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

@Suppress("ClassName", "EnumEntryName")
enum class searchBar {
  query,
  results,
  suggestions,
  search_error;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

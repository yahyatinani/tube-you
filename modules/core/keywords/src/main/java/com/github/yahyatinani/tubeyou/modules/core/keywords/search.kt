package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class search {
  // FSM events/triggers:
  show_search_bar,
  update_search_input,
  clear_search_input,
  submit,
  set_suggestions,
  set_search_results,
  back_press_search,

  fsm,
  route,
  stack,
  get_search_results,
  get_search_suggestions,
  view_model,
  sb_state,
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

package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class search {
  // FSM events/triggers:
  show_search_bar,
  update_search_input,
  clear_search_input,
  submit,
  activate_searchBar,
  set_suggestions,
  set_search_results,
  back_press_search,

  route,
  stack,
  get_search_results,
  get_search_suggestions,
  view_model,
  search_bar,
  search_list,
  coroutine_scope,
  panel_fsm;

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

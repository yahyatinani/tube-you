package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class common {
  init_app_db,
  navigate_to,
  on_click_nav_item,
  expand_top_app_bar,

  icon,
  destination,
  navOptions,

  //  is_search_bar_active,
  back_press_top_nav,
  pop_back_stack,
  is_top_backstack_empty,
  start_destination,
  play_video,
  expand_player_sheet,
  toggle_player,
  close_player,
  hide_player_sheet,
  state,
  minimize_player,
  collapse_player_sheet,
  comment_replies,
  is_route_active,
  prev_top_nav_route,
  auto_scroll_up,
  error,
  show_top_settings_popup,
  hide_top_settings_popup,
  is_loading;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

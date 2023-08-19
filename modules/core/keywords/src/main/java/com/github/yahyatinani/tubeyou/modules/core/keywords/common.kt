package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class common {
  init_app_db,
  navigate_to,
  is_backstack_available,
  navigation_items,
  active_navigation_item,
  on_click_nav_item,
  expand_top_app_bar,

  label_text_id,
  icon_content_desc_text_id,
  is_selected,
  icon_variant,
  icon,
  destination,
  navOptions,
  is_online,

  //  is_search_bar_active,
  back_press_top_nav,
  pop_back_stack,
  is_top_backstack_empty,
  start_destination,
  play_video,
  expand_player_sheet,
  coroutine_scope,
  toggle_player,
  active_stream,
  close_player,
  hide_player_sheet,
  state,
  minimize_player,
  collapse_player_sheet,
  comment_replies,
  is_route_active,
  prev_top_nav_route;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

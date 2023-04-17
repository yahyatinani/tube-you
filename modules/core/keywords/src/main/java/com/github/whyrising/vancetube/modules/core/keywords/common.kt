package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class common {
  initialize,
  navigate_to,
  set_backstack_status,
  is_backstack_available,
  api_endpoint,
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
  dispatch_debounce,
  is_search_bar_active,
  search_bar,
  is_search_bar_visible,
  back_press,
  search_back_press,
  pop_back_stack,
  is_backstack_empty,
  clear_search_text,
  search_bar_bak,
  show_search_bar,
  search_suggestions,
  set_suggestions,
  search_query;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

@Suppress("ClassName", "EnumEntryName")
enum class searchBar {
  query,
  results,
  suggestions;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

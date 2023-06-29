package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class common {
  initialize,
  navigate_to,
  is_backstack_available,
  api_url,
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
  bottom_bar_back_press,
  pop_back_stack,
  is_backstack_empty,
  start_destination;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

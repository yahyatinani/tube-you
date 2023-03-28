package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class common {
  initialize,
  navigate_to,
  go_back,
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
  current_back_stack_id,
  dispatch_debounce;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

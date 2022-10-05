package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class base {
  initialise,
  start_route,
  navigate_to,
  go_back,
  set_backstack_status,
  is_backstack_available,
  api,
  bottom_nav_items,
  current_bottom_nav_panel,
  on_bottom_nav_click,
  expand_top_app_bar,

  label_text_id,
  icon_content_desc_text_id,
  is_selected,
  icon_variant,
  icon;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

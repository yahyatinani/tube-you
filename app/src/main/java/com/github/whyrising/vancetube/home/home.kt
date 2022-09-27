package com.github.whyrising.vancetube.home

enum class home {
  route,
  panel,
  state,
  view_model,
  set_popular_vids,
  refresh,
  load_popular_videos,
  go_top_list,
  popular_vids;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class home {
  view_model,
  coroutine_scope,
  fsm_state,
  fsm,
  content,

  // FSM events
  load,
  refresh,
  set_loading_results,
  set_loading_error;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

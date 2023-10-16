package io.github.yahyatinani.tubeyou.db

import io.github.yahyatinani.tubeyou.BuildConfig
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.y.core.m

val defaultAppState = m(
  ty_db.active_top_level_route to HOME_GRAPH_ROUTE,
  ty_db.top_level_back_handler_enabled to false,
  ty_db.api_url to "https://ytapi.dc09.ru",
  ty_db.is_top_settings_popup_visible to false,
  ty_db.app_version to BuildConfig.VERSION_NAME
)

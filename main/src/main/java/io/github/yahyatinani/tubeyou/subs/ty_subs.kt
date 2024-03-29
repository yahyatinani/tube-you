package io.github.yahyatinani.tubeyou.subs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.tubeyou.common.ty_db.is_top_settings_popup_visible
import io.github.yahyatinani.y.core.get

@Composable
fun RegTySubs() {
  remember {
    regSub<AppDb>(common.is_route_active) { db, (_, navItem) ->
      db[ty_db.active_top_level_route] == navItem
    }

    regSub(
      queryId = ty_db.top_level_back_handler_enabled,
      key = ty_db.top_level_back_handler_enabled
    )

    regSub(
      queryId = is_top_settings_popup_visible,
      key = is_top_settings_popup_visible
    )

    true
  }
}

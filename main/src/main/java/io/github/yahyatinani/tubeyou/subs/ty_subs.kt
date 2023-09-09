package io.github.yahyatinani.tubeyou.subs

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TopAppBarActionItem
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.tubeyou.R
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v

@Composable
fun RegTySubs() {
  remember {
    regSub<AppDb>(":active_route") { db, _ ->
      db[ty_db.active_top_level_route]
    }

    regSub<AppDb>(common.is_route_active) { db, (_, navItem) ->
      db[ty_db.active_top_level_route] == navItem
    }

    regSub(
      queryId = ty_db.top_level_back_handler_enabled,
      key = ty_db.top_level_back_handler_enabled
    )

    regSub<AppDb>(common.top_app_bar_actions) { _, (_, resources) ->
      v(
        TopAppBarActionItem(
          icon = TyIcons.Search,
          iconContentDescription = (resources as Resources).getString(
            R.string.top_app_bar_search_action_icon_description
          ),
          onActionClick = {
            dispatch(v(search.panel_fsm, search.show_search_bar))
          }
        ),
        TopAppBarActionItem(
          icon = TyIcons.AccountCircle,
          iconContentDescription = resources.getString(
            R.string.top_app_bar_settings_action_icon_description
          ),
          onActionClick = { /* TODO: settings */ }
        )
      )
    }

    true
  }
}

package io.github.yahyatinani.tubeyou.subs

import android.content.res.Resources
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TopAppBarActionItem
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.tubeyou.R
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.tubeyou.modules.feature.library.YOU_GRAPH_ROUTE
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v

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

    regSub<AppDb>(common.top_app_bar_actions) { db, (_, resources) ->
      v(
        TopAppBarActionItem(
          icon = Icons.Default.Search,
          iconContentDescription = (resources as Resources).getString(
            R.string.top_app_bar_search_action_icon_description
          ),
          onActionClick = {
            dispatchSync(v(search.panel_fsm, search.show_search_bar))
          }
        )
      ).let {
        if (db[ty_db.active_top_level_route] != YOU_GRAPH_ROUTE) {
          return@regSub it
        }

        it.conj(
          TopAppBarActionItem(
            icon = Icons.Outlined.Settings,
            iconContentDescription = resources.getString(
              R.string.top_app_bar_settings_action_icon_description
            ),
            onActionClick = { /* TODO: settings */ }
          )
        )
      }
    }

    true
  }
}

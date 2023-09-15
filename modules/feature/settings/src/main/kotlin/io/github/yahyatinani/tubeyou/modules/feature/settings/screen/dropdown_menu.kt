package io.github.yahyatinani.tubeyou.modules.feature.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.common.ty_db
import io.github.yahyatinani.y.core.v

@Composable
fun SettingsDropdownButton(tint: Color) {
  Box {
    val iconDesc = "Account"
    IconButton(
      modifier = Modifier.testTag(iconDesc),
      onClick = { dispatch(v(common.show_top_settings_popup)) }
    ) {
      Icon(
        imageVector = TyIcons.NoAccounts,
        contentDescription = iconDesc,
        modifier = Modifier.size(30.dp),
        tint = tint
      )
    }

    DropdownMenu(
      expanded = watch(query = v(ty_db.is_top_settings_popup_visible)),
      onDismissRequest = {
        dispatch(v(common.hide_top_settings_popup))
      },
      modifier = Modifier.background(TyTheme.colors.popupContainer)
    ) {
      val dropDownItemIconSize = 28.dp
      DropdownMenuItem(
        text = { Text("Login/Register") },
        onClick = { /*TODO:*/ },
        leadingIcon = {
          Icon(
            TyIcons.Login,
            contentDescription = null,
            modifier = Modifier.size(dropDownItemIconSize)
          )
        }
      )
      DropdownMenuItem(
        text = { Text("Settings") },
        onClick = { /*TODO:*/ },
        leadingIcon = {
          Icon(
            TyIcons.Settings,
            contentDescription = null,
            modifier = Modifier.size(dropDownItemIconSize)
          )
        }
      )
      Divider()
      DropdownMenuItem(
        text = { Text("About") },
        onClick = {
          dispatch(v("on_dropdown_item_about_click"))
        },
        leadingIcon = {
          Icon(
            TyIcons.Info,
            contentDescription = null,
            modifier = Modifier.size(dropDownItemIconSize)
          )
        }
      )
    }
  }
}

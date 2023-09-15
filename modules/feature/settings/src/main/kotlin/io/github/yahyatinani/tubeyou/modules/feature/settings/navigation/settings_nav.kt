package io.github.yahyatinani.tubeyou.modules.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SETTINGS_ROUTE = "about_route"

fun NavGraphBuilder.settingsScreen() {
  composable(route = SETTINGS_ROUTE) {
  }
}

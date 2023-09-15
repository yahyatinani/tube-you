package io.github.yahyatinani.tubeyou.modules.feature.settings.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.tubeyou.modules.feature.settings.screen.AboutRoute
import io.github.yahyatinani.tubeyou.modules.feature.settings.subs.RegAboutSubs
import io.github.yahyatinani.y.core.v

const val ABOUT_ROUTE = "about_route"

@Composable
fun InitAbout() {
  RegAboutSubs()
}

fun NavGraphBuilder.aboutScreen() {
  composable(route = ABOUT_ROUTE) {
    InitAbout()
    AboutRoute(
      onNavIconClick = {
        dispatch(v("on_about_nav_ic_click"))
      }
    )
  }
}

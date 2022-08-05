package com.github.whyrising.composetemplate.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import com.github.whyrising.composetemplate.about.about
import com.github.whyrising.composetemplate.base.base
import com.github.whyrising.composetemplate.ui.anim.enterAnimation
import com.github.whyrising.composetemplate.ui.anim.exitAnimation
import com.github.whyrising.composetemplate.ui.theme.TemplateTheme
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.recompose.w
import com.github.whyrising.y.core.v
import com.google.accompanist.navigation.animation.composable

@Composable
fun Home(modifier: Modifier = Modifier) {
  Surface(modifier = modifier.fillMaxSize()) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Button(
        onClick = { dispatch(v(home.inc_count)) }
      ) {
        Text(text = subscribe<String>(v(home.btn_count_name)).w())
      }
      Button(
        onClick = { dispatch(v(base.navigate, about.about_panel)) }
      ) {
        Text(text = "About")
      }
    }
  }
}

// -- navigation ---------------------------------------------------------------

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.home(animOffSetX: Int) {
  composable(
    route = home.home_panel.name,
    exitTransition = { exitAnimation(targetOffsetX = -animOffSetX) },
    popEnterTransition = { enterAnimation(initialOffsetX = -animOffSetX) }
  ) {
    Home()
  }
}

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun HomePreview() {
  TemplateTheme {
    Home()
  }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeDarkPreview() {
  TemplateTheme {
    Home()
  }
}

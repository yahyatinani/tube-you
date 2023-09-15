package io.github.yahyatinani.tubeyou.modules.feature.settings.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.modules.feature.settings.R
import io.github.yahyatinani.y.core.v

@Composable
fun AboutScreen(appVersion: String, onNavIconClick: () -> Unit) {
  Scaffold(
    topBar = {
      FullScreenTopAppBar(
        title = "About",
        onNavIconClick = onNavIconClick
      )
    }
  ) { p ->
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(p)
    ) {
      Column {
        val color = MaterialTheme.colorScheme.onSurface.copy(.5f)
        val style = MaterialTheme.typography.bodyMedium.copy(color = color)
        val uriHandler = LocalUriHandler.current
        val gitHubUrl = "github.com/yahyatinani/tube-you"
        ListItem(
          headlineContent = {
            Text(text = "GitHub")
          },
          modifier = Modifier.clickable {
            uriHandler.openUri("https://www.$gitHubUrl")
          },
          supportingContent = {
            Text(
              text = gitHubUrl,
              style = style
            )
          }
        )
        ListItem(
          headlineContent = {
            Text(text = "Backend")
          },
          modifier = Modifier.clickable {
            uriHandler.openUri("https://www.github.com/TeamPiped/piped")
          },
          supportingContent = {
            Text(
              text = "Piped",
              style = style
            )
          }
        )
        ListItem(
          headlineContent = {
            Text(text = "License")
          },
          modifier = Modifier.clickable {
            uriHandler.openUri(
              "https://raw.githubusercontent.com/yahyatinani/tube-you/" +
                "main/LICENSE"
            )
          },
          supportingContent = {
            Text(
              text = "GPL-3.0",
              style = style
            )
          }
        )
        ListItem(
          headlineContent = {
            Text(text = stringResource(R.string.app_version_label))
          },
          modifier = Modifier.clickable { },
          supportingContent = {
            Text(
              text = appVersion,
              style = style
            )
          }
        )
      }
    }
  }
}

@Composable
fun AboutRoute(
  appVersion: String = watch(query = v(":app_version")),
  onNavIconClick: () -> Unit
) {
  AboutScreen(
    appVersion = appVersion,
    onNavIconClick = onNavIconClick
  )
}

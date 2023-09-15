package io.github.yahyatinani.tubeyou.modules.feature.settings.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun FullScreenTopAppBar(title: String, onNavIconClick: () -> Unit) {
  TopAppBar(
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium
      )
    },
    navigationIcon = {
      IconButton(
        onClick = onNavIconClick,
        modifier = Modifier.padding(end = 16.dp)
      ) {
        Icon(
          imageVector = TyIcons.ArrowBack,
          modifier = Modifier.size(28.dp),
          contentDescription = "Navigate back"
        )
      }
    }
  )
}

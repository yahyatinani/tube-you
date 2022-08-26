package com.github.whyrising.vancetube.ui.theme.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.y.core.v

@Composable
fun BackArrow() {
  IconButton(
    onClick = {
      dispatch(v(base.navigate, base.go_back))
    }
  ) {
    Icon(
      imageVector = Icons.Filled.ArrowBack,
      contentDescription = "Back"
    )
  }
}

@Composable
fun SmallLabelText(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.caption
  )
}

@Composable
fun PageCircularProgressIndicator(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    CircularProgressIndicator(color = Color.Cyan)
  }
}

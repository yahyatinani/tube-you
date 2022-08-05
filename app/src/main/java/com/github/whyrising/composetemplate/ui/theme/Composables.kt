package com.github.whyrising.composetemplate.ui.theme

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import com.github.whyrising.composetemplate.base.base
import com.github.whyrising.recompose.dispatch
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

package com.github.yahyatinani.tubeyou.modules.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.R

@Composable
fun MoreButton(modifier: Modifier = Modifier) {
  IconButton(
    modifier = modifier.size(20.dp),
    onClick = { /*TODO*/ }
  ) {
    Icon(
      imageVector = Icons.Filled.MoreVert,
      contentDescription = "more"
    )
  }
}

@Composable
fun SubscribeButton(modifier: Modifier = Modifier) {
  val colorScheme = MaterialTheme.colorScheme
  val typography = MaterialTheme.typography

  Button(
    modifier = modifier,
    onClick = { /*TODO*/ },
    colors = ButtonDefaults.buttonColors(
      containerColor = colorScheme.onSurface,
      contentColor = colorScheme.surface
    ),
    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
  ) {
    Text(
      text = stringResource(R.string.subscribe),
      style = typography.labelMedium
    )
  }
}

@Composable
fun SubscribeButton(
  modifier: Modifier = Modifier,
  containerColor: Color,
  contentColor: Color,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier,
    onClick = onClick,
    color = containerColor,
    contentColor = contentColor,
    shape = RoundedCornerShape(20.dp)
  ) {
    Text(
      text = stringResource(R.string.subscribe),
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
      style = MaterialTheme.typography.labelMedium
    )
  }
}

package com.github.yahyatinani.tubeyou.modules.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
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
fun TyRoundedButton(
  modifier: Modifier = Modifier,
  containerColor: Color,
  contentColor: Color,
  onClick: () -> Unit,
  content: @Composable () -> Unit
) {
  Surface(
    modifier = modifier,
    onClick = onClick,
    color = containerColor,
    contentColor = contentColor,
    shape = RoundedCornerShape(20.dp),
    content = content
  )
}

@Composable
fun SubscribeButton(
  text: String,
  modifier: Modifier = Modifier,
  containerColor: Color,
  contentColor: Color,
  onClick: () -> Unit
) {
  TyRoundedButton(
    modifier = modifier,
    containerColor = containerColor,
    contentColor = contentColor,
    onClick = onClick
  ) {
    Text(
      modifier = modifier.padding(horizontal = 10.dp, vertical = 7.dp),
      text = text,
      style = MaterialTheme.typography.labelMedium
    )
  }
}

@Composable
fun TyIconRoundedButton(
  text: String,
  modifier: Modifier = Modifier,
  textStyle: TextStyle,
  onClick: () -> Unit,
  containerColor: Color,
  horizontal: Dp,
  vertical: Dp,
  icon: @Composable () -> Unit
) {
  Surface(
    modifier = modifier,
    color = containerColor,
    shape = RoundedCornerShape(20.dp)
  ) {
    Row(
      modifier = Modifier
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = rememberRipple(),
          enabled = true,
          onClick = onClick
        )
        .padding(horizontal = horizontal, vertical = vertical)
        .height(IntrinsicSize.Min),
      verticalAlignment = Alignment.CenterVertically
    ) {
      icon()
      Spacer(modifier = Modifier.width(8.dp))
      Text(text = text, style = textStyle)
    }
  }
}

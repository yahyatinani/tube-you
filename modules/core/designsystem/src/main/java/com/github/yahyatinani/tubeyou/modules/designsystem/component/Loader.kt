package com.github.yahyatinani.tubeyou.modules.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue300

@Composable
fun AppendingLoader() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    CircularProgressIndicator(
      modifier = Modifier.align(Alignment.Center),
      color = Blue300
    )
  }
}

@Composable
fun TextLoader(
  modifier: Modifier = Modifier,
  containerColor: Color
) {
  Surface(
    modifier = modifier.height(16.dp),
    shape = RoundedCornerShape(6.dp),
    color = containerColor,
    content = {}
  )
}

@Composable
fun AvatarLoader(
  modifier: Modifier = Modifier,
  containerColor: Color
) {
  Surface(
    modifier = modifier,
    shape = CircleShape,
    color = containerColor,
    content = {}
  )
}

@Composable
fun StreamLoaderPortrait(containerColor: Color) {
  Column {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16 / 9f),
      color = containerColor,
      content = {}
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
    ) {
      AvatarLoader(
        modifier = Modifier.size(size = 32.dp),
        containerColor = containerColor
      )

      Spacer(modifier = Modifier.width(16.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(end = 16.dp)
      ) {
        TextLoader(
          modifier = Modifier.fillMaxWidth(),
          containerColor = containerColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextLoader(
          modifier = Modifier.width(112.dp),
          containerColor = containerColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextLoader(
          modifier = Modifier.width(56.dp),
          containerColor = containerColor
        )
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

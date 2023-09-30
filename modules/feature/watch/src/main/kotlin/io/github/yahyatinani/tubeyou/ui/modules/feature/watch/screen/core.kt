package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons

private fun Context.findWindow(): Window? {
  var context = this
  while (context is ContextWrapper) {
    if (context is Activity) return context.window
    context = context.baseContext
  }
  return null
}

@Composable
fun DragHandle(modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
  Surface(
    modifier = modifier
      .wrapContentSize()
      .padding(top = 8.dp, bottom = 2.dp)
      .semantics { contentDescription = "dragHandleDescription" },
    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .2f),
    shape = MaterialTheme.shapes.extraLarge
  ) {
    Box(
      Modifier
        .clickable(onClick = onClick)
        .size(width = 40.dp, height = 4.0.dp)
    )
  }
}

@Composable
fun SheetHeader(
  modifier: Modifier = Modifier,
  headerTitle: String = "",
  header: (@Composable () -> Unit)? = null,
  isSheetOpen: Boolean,
  closeSheet: () -> Unit,
  toggleExpansion: () -> Unit
) {
  val density = LocalDensity.current
  val screenWidthDp = LocalConfiguration.current.screenWidthDp
  val halfScreenWidth =
    remember(screenWidthDp) { with(density) { screenWidthDp.dp.toPx() } / 2 }
  val handlerWidth = remember(density) { with(density) { 40.dp.toPx() } }
  val x1 = remember(halfScreenWidth) { halfScreenWidth - handlerWidth }
  val x2 = remember(halfScreenWidth) { halfScreenWidth + handlerWidth }
  Column(
    modifier = Modifier.pointerInput(Unit) {
      detectTapGestures(
        onTap = { offset: Offset ->
          val (x, _) = offset
          if (x > x1 && x < x2) {
            toggleExpansion()
          }
        }
      )
    }
  ) {
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(end = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        when {
          header != null -> header()
          else -> {
            Text(
              text = headerTitle,
              style = MaterialTheme.typography.titleMedium
            )
          }
        }
      }
      IconButton(onClick = closeSheet) {
        Icon(
          modifier = Modifier
            .size(32.dp)
            .testTag("watch:close_comments_desc_sheet"),
          imageVector = TyIcons.Close,
          contentDescription = ""
        )
      }
    }
    Divider(modifier = Modifier.fillMaxWidth())
  }

  BackHandler(enabled = isSheetOpen, onBack = closeSheet)
}

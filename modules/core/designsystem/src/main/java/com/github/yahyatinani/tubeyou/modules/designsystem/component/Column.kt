package com.github.yahyatinani.tubeyou.modules.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeadedSheetColumn(
  modifier: Modifier = Modifier,
  sheetState: SheetState,
  sheetPeekHeight: Dp,
  header: @Composable () -> Unit,
  content: @Composable (ColumnScope.() -> Unit)
) {
  Column(
    modifier = when (sheetState.currentValue) {
      SheetValue.PartiallyExpanded -> {
        modifier.height(remember(sheetPeekHeight) { sheetPeekHeight - 24.dp })
      }

      else -> modifier
    }
  ) {
    header()
    content()
  }
}
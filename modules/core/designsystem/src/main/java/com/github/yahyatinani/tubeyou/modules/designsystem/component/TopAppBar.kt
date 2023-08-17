package com.github.yahyatinani.tubeyou.modules.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Immutable
data class TopAppBarActionItem(
  val icon: ImageVector,
  val iconContentDescription: String? = null,
  val onActionClick: () -> Unit = {}
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TyTopAppBar(title: String, actions: List<TopAppBarActionItem>) {
  val colorScheme = MaterialTheme.colorScheme
  TopAppBar(
    title = { Text(text = title, fontWeight = FontWeight.Bold) },
    modifier = Modifier.fillMaxWidth(),
    actions = {
      actions.forEach { action ->
        IconButton(onClick = action.onActionClick) {
          Icon(
            imageVector = action.icon,
            contentDescription = action.iconContentDescription,
            tint = colorScheme.onSurface
          )
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      scrolledContainerColor = colorScheme.background
    )
//          scrollBehavior = scrollBehavior
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TySearchBar(
  searchQuery: String,
  onQueryChange: (String) -> Unit,
  onSearch: (String) -> Unit,
  isActive: Boolean,
  onActiveChange: (Boolean) -> Unit,
  clearInput: () -> Unit,
  backPress: () -> Unit,
  suggestions: List<String>,
  colorScheme: ColorScheme,
  onSuggestionSelected: (suggestion: String) -> Unit
) {
  val focusRequester = FocusRequester()
  val placeHolderColor = colorScheme.onSurface.copy(alpha = .6f)
  SearchBar(
    query = searchQuery,
    modifier = Modifier
      .fillMaxWidth()
      .focusRequester(focusRequester),
    active = isActive,
    tonalElevation = 0.dp,
    shape = RoundedCornerShape(30.dp),
    colors = SearchBarDefaults.colors(
      inputFieldColors = SearchBarDefaults.inputFieldColors(
        focusedPlaceholderColor = placeHolderColor,
        unfocusedPlaceholderColor = placeHolderColor
      )
    ),
    placeholder = { Text(text = "Search YouTube") },
    leadingIcon = {
      IconButton(onClick = backPress) {
        Icon(
          imageVector = Icons.Filled.ArrowBack,
          modifier = Modifier,
          contentDescription = ""
        )
      }
    },
    trailingIcon = {
      IconButton(onClick = clearInput) {
        Icon(
          imageVector = Icons.Filled.Close,
          modifier = Modifier,
          contentDescription = ""
        )
      }
    },
    onQueryChange = onQueryChange,
    onActiveChange = onActiveChange,
    onSearch = onSearch
  ) {
    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
      itemsIndexed(
        key = { i, _ -> i },
        items = suggestions
      ) { _, str ->
        SearchSuggestionItem(text = str) {
          onSuggestionSelected(str)
        }
      }
    }
  }

  LaunchedEffect(isActive) {
    if (isActive) focusRequester.requestFocus()
  }
}

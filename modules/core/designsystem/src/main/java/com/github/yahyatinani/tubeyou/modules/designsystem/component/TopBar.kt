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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

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
  androidx.compose.material3.SearchBar(
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
    if (isActive) {
      focusRequester.requestFocus()
    }
  }
}

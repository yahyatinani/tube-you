package com.github.yahyatinani.tubeyou.modules.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TyTopAppBar(
  title: String,
  actions: @Composable (RowScope.() -> Unit),
  scrollBehavior: TopAppBarScrollBehavior
) {
  val colorScheme = MaterialTheme.colorScheme
  TopAppBar(
    title = { Text(text = title, fontWeight = FontWeight.Bold) },
    modifier = Modifier.fillMaxWidth(),
    actions = actions,
    colors = TopAppBarDefaults.topAppBarColors(
      scrolledContainerColor = colorScheme.background
    ),
    scrollBehavior = scrollBehavior
  )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TySearchBar(
  searchQuery: String,
  isSearchBarActive: Boolean,
  colors: SearchBarColors = MaterialTheme.colorScheme.let { colorScheme ->
    remember(colorScheme) { colorScheme.onSurface.copy(.6f) }.let {
      SearchBarDefaults.colors(
        inputFieldColors = SearchBarDefaults.inputFieldColors(
          focusedPlaceholderColor = it,
          unfocusedPlaceholderColor = it
        )
      )
    }
  },
  onQueryChange: (updatedQuery: String) -> Unit,
  onSearchClick: (searchedQuery: String) -> Unit,
  onActiveChange: (isActive: Boolean) -> Unit,
  onTrailingClick: () -> Unit,
  onLeadingClick: () -> Unit,
  suggestions: List<String>,
  onSuggestionClick: (suggestion: String) -> Unit
) {
  val focusRequester = remember { FocusRequester() }
  val trailing: @Composable () -> Unit = {
    IconButton(
      modifier = Modifier.testTag("clear_search_input"),
      onClick = onTrailingClick
    ) {
      Icon(
        imageVector = TyIcons.Close,
        modifier = Modifier.size(24.dp),
        contentDescription = "Clear search query"
      )
    }
  }
  val isQueryEmpty = searchQuery.isEmpty()
  SearchBar(
    query = searchQuery,
    modifier = Modifier
      .testTag("search:search_bar")
      .fillMaxWidth()
      .focusRequester(focusRequester),
    active = isSearchBarActive,
    tonalElevation = 0.dp,
    shape = RoundedCornerShape(30.dp),
    colors = colors,
    placeholder = {
      Text(
        text = "Search YouTube",
        style = MaterialTheme.typography.bodyLarge
      )
    },
    leadingIcon = {
      IconButton(onClick = onLeadingClick) {
        Icon(
          imageVector = TyIcons.ArrowBack,
          modifier = Modifier.size(28.dp),
          contentDescription = "Navigate back"
        )
      }
    },
    trailingIcon = remember(isQueryEmpty) {
      if (isQueryEmpty) null else trailing
    },
    onQueryChange = onQueryChange,
    onActiveChange = onActiveChange,
    onSearch = onSearchClick
  ) {
    LazyColumn(
      modifier = Modifier
        .padding(8.dp)
        .testTag("search:suggestions_list")
    ) {
      itemsIndexed(
        key = { i, _ -> i },
        items = suggestions
      ) { _, str ->
        SearchSuggestionItem(text = str) {
          onSuggestionClick(str)
        }
      }
    }
  }

  LaunchedEffect(isSearchBarActive) {
    if (isSearchBarActive) focusRequester.requestFocus()
  }
}

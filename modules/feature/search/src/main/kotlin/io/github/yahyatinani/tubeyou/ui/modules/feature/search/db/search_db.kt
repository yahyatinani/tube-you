package io.github.yahyatinani.tubeyou.ui.modules.feature.search.db

import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

typealias SearchBar = IPersistentMap<Any, Any?>
typealias SearchStack = PersistentVector<SearchBar>

val defaultSb: IPersistentMap<Any, Any> = m(
  searchBar.query to "",
  searchBar.suggestions to v<String>()
)

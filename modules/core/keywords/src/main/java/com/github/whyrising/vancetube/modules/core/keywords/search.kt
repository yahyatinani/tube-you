package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName", "EnumEntryName")
enum class search {
  route;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

package com.github.whyrising.vancetube.trends

@Suppress("ClassName")
enum class trends {
  route;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

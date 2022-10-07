package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName")
enum class library {
  route;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

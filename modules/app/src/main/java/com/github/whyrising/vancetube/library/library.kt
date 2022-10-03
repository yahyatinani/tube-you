package com.github.whyrising.vancetube.library

@Suppress("ClassName")
enum class library {
  route;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

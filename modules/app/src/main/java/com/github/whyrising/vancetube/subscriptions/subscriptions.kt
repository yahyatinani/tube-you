package com.github.whyrising.vancetube.subscriptions

@Suppress("ClassName")
enum class subscriptions {
  route;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

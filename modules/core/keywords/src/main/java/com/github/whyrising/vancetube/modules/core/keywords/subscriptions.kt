package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName")
enum class subscriptions {
  route;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

val SUBSCRIPTION_ROUTE = subscriptions.route.toString()

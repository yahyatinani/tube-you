package com.github.yahyatinani.tubeyou.modules.core.keywords

@Suppress("ClassName")
enum class subscriptions {
  route;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

const val SUBSCRIPTIONS_GRAPH_ROUTE = "subscriptions_graph"
const val SUBSCRIPTIONS_ROUTE = "subscriptions_route"

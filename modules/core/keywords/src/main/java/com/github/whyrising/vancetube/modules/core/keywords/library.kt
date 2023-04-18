package com.github.whyrising.vancetube.modules.core.keywords

@Suppress("ClassName")
enum class library {
  route;

  override fun toString(): String = ":${javaClass.simpleName}/$name"
}

const val LIBRARY_GRAPH_ROUTE = "library_graph"
const val LIBRARY_ROUTE = "library_route"

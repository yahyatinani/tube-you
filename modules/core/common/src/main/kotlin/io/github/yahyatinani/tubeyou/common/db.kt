package io.github.yahyatinani.tubeyou.common

import io.github.yahyatinani.recompose.cofx.Coeffects
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get

typealias AppDb = IPersistentMap<Any, Any>

fun appDbBy(cofx: Coeffects): AppDb = get(cofx, recompose.db)!!

fun activeTopLevelRoute(appDb: AppDb): Any =
  appDb[ty_db.active_top_level_route]!!

@Suppress("EnumEntryName", "ClassName")
enum class ty_db {
  active_top_level_route,
  top_level_back_handler_enabled,
  api_url
}

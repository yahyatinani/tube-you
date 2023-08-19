package io.github.yahyatinani.tubeyou.modules.core.network

import io.github.yahyatinani.recompose.pagingfx.Page
import io.github.yahyatinani.y.core.collections.PersistentVector
import kotlinx.serialization.Serializable
import java.net.URLEncoder

@Serializable
data class SearchResponse(
  val items: PersistentVector<Searchable>,
  val nextpage: String? = null,
  val suggestion: String? = null,
  val corrected: Boolean = false
) : Page {
  override val data: List<Searchable> = items
  override val prevKey: String? = null
  override val nextKey: String? = if (nextpage != "null" && nextpage != null) {
    URLEncoder.encode(nextpage, "UTF-8")
  } else {
    null
  }
}

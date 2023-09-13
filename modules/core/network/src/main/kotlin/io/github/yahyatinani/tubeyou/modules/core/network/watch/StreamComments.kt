package io.github.yahyatinani.tubeyou.modules.core.network.watch

import androidx.compose.runtime.Immutable
import io.github.yahyatinani.recompose.pagingfx.Page
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.net.URLEncoder

@Immutable
@Serializable
data class StreamComments(
  val comments: List<StreamComment>,
  val nextpage: String? = null,
  val disabled: Boolean,
  val commentCount: Long
) : Page {

  @Transient
  override val data = comments

  @Transient
  override val nextKey: String? = if (nextpage != "null" && nextpage != null) {
    URLEncoder.encode(nextpage, "UTF-8")
  } else {
    null
  }

  @Transient
  override val prevKey: String? = null
}

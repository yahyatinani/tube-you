package com.github.whyrising.vancetube.modules.panel.home

import androidx.compose.runtime.Immutable
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.ids.coeffects
import com.github.whyrising.recompose.ids.recompose
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import kotlinx.serialization.Serializable

typealias AppDb = IPersistentMap<Any, Any>

/**
 * Spec
 *
 * e.g:
 * {:state [States.Loading]
 * :popular_vids ([VideoData])}
 */

@Serializable
data class ThumbnailData(val url: String)

@Immutable
@Serializable
data class VideoData(
  val videoId: String,
  val title: String,
  val videoThumbnails: List<ThumbnailData>,
  val lengthSeconds: Int,
  val author: String,
  val authorId: String,
  val viewCount: Long,
  val publishedText: String
)

// -- cofx Registrations -------------------------------------------------------

val regCofx by lazy {
  regCofx(home.fsm) { cofx ->
    val (eventId) = cofx[coeffects.originalEvent] as PersistentVector<Any>
    val nextDb = updateToNextState(getAppDb(cofx), eventId)
    cofx.assoc(recompose.db, nextDb)
  }
}

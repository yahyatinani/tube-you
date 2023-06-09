package com.github.yahyatinani.tubeyou.modules.panel.common.search

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.whyrising.recompose.dispatchSync
import com.github.whyrising.recompose.watch
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ChannelItem
import com.github.yahyatinani.tubeyou.modules.designsystem.component.Panel
import com.github.yahyatinani.tubeyou.modules.designsystem.component.PlayListLandscape
import com.github.yahyatinani.tubeyou.modules.designsystem.component.PlayListPortrait
import com.github.yahyatinani.tubeyou.modules.designsystem.component.VideoItemLandscapeCompact
import com.github.yahyatinani.tubeyou.modules.designsystem.component.VideoItemPortrait
import com.github.yahyatinani.tubeyou.modules.designsystem.data.ChannelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PlaylistVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos

const val SEARCH_ROUTE = "search_results"

@Composable
fun SearchPanel(
  listState: LazyListState,
  videos: Videos,
  triggerAppending: Any?,
  orientation: Int,
  thumbnailHeight: Dp,
  appendLoader: @Composable () -> Unit
) {
  LazyColumn(
    state = listState,
    modifier = Modifier
      .testTag("search_list")
      .fillMaxSize()
  ) {
    // Pagination Loading UI
    val items = videos.value
    itemsIndexed(items = items) { index: Int, vm: Any ->
      if (triggerAppending != null) dispatchSync(v(triggerAppending, index))

      val isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT
      if (vm is VideoViewModel) {
        if (
          index > 1 &&
          items[index - 1] as? VideoViewModel != null &&
          isPortrait
        ) {
          Divider(thickness = 6.dp, color = Color.DarkGray)
        }
        when {
          isPortrait -> VideoItemPortrait(
            viewModel = vm,
            thumbnailHeight = thumbnailHeight
          )

          else -> VideoItemLandscapeCompact(
            viewModel = vm,
            thumbnailHeight = thumbnailHeight
          )
        }
      } else {
        if (index != 0 && isPortrait) {
          Divider(thickness = 6.dp, color = Color.DarkGray)
        }

        when (vm) {
          is ChannelVm -> ChannelItem(
            vm = vm,
            modifier = Modifier
              .clickable { /*TODO*/ }
              .fillMaxWidth(),
            avatarPaddingValues = PaddingValues(
              horizontal = (if (isPortrait) 0 else 24).dp
            )
          )

          is PlaylistVm -> {
            when {
              isPortrait -> PlayListPortrait(
                modifier = Modifier.padding(start = 12.dp),
                viewModel = vm,
                thumbnailHeight = thumbnailHeight
              )

              else -> PlayListLandscape(
                viewModel = vm,
                thumbnailHeight = thumbnailHeight
              )
            }
          }
        }
      }
    }
    item { appendLoader() }
  }
}

fun NavGraphBuilder.searchPanel(
  route: String,
  orientation: Int,
  thumbnailHeight: Dp
) {
  composable(route = "$route/$SEARCH_ROUTE") {
    val panelVm =
      watch<PanelVm>(v(search.view_model, LocalContext.current.resources))

    Panel(panelVm = panelVm) { videos, triggerAppending, appendLoader ->
      SearchPanel(
        listState = rememberLazyListState(),
        videos = videos,
        triggerAppending = triggerAppending,
        orientation = orientation,
        thumbnailHeight = thumbnailHeight,
        appendLoader = appendLoader
      )
    }
  }
}

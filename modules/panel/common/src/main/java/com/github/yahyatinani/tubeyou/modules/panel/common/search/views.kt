package com.github.yahyatinani.tubeyou.modules.panel.common.search

import android.content.res.Configuration.ORIENTATION_PORTRAIT
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
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
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
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.y.core.v

const val SEARCH_ROUTE = "search_results"

@Composable
fun PortraitListItem(
  vm: Any,
  thumbnailHeight: Dp,
  index: Int,
  lastIndex: Int
) {
  when (vm) {
    is VideoViewModel -> {
      VideoItemPortrait(
        viewModel = vm,
        thumbnailHeight = thumbnailHeight
      ) { url: String, thumbnail: String ->
        dispatch(v(common.play_video, url, thumbnail))
      }
    }

    is ChannelVm -> {
      if (index != 0) Divider(color = DarkGray)

      ChannelItem(
        modifier = Modifier
          .clickable { /*TODO*/ }
          .fillMaxWidth(),
        vm = vm
      )

      if (index != lastIndex) Divider(color = DarkGray)
    }

    else -> PlayListPortrait(
      modifier = Modifier.padding(start = 12.dp),
      viewModel = vm as PlaylistVm,
      thumbnailHeight = thumbnailHeight
    )
  }
}

@Composable
fun LandscapeListItem(
  vm: Any,
  thumbnailHeight: Dp
) {
  when (vm) {
    is VideoViewModel -> VideoItemLandscapeCompact(
      viewModel = vm,
      thumbnailHeight = thumbnailHeight
    )

    is ChannelVm -> ChannelItem(
      vm = vm,
      modifier = Modifier
        .clickable { /*TODO*/ }
        .fillMaxWidth(),
      avatarPaddingValues = PaddingValues(horizontal = 24.dp)
    )

    else -> PlayListLandscape(vm as PlaylistVm, thumbnailHeight)
  }
}

@Composable
fun SearchPanel(
  listState: LazyListState,
  videos: Videos,
  triggerAppending: Any?,
  isPortrait: Boolean,
  thumbnailHeight: Dp,
  appendLoader: @Composable () -> Unit
) {
  LazyColumn(
    state = listState,
    modifier = Modifier
      .testTag("search_results_list")
      .fillMaxSize()
  ) {
    itemsIndexed(items = videos.value) { index: Int, vm: Any ->
      dispatch(v(triggerAppending!!, index))
      when {
        isPortrait -> PortraitListItem(
          vm = vm,
          thumbnailHeight = thumbnailHeight,
          index = index,
          lastIndex = videos.value.size
        )

        else -> LandscapeListItem(vm, thumbnailHeight)
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
        isPortrait = orientation == ORIENTATION_PORTRAIT,
        thumbnailHeight = thumbnailHeight,
        appendLoader = appendLoader
      )
    }
  }
}

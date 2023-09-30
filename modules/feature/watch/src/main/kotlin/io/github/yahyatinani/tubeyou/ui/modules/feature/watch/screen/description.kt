package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AuthorAvatar
import io.github.yahyatinani.recompose.RegFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs.Stream
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch

@Composable
fun DescriptionSection(
  modifier: Modifier = Modifier,
  streamTitle: String,
  views: String,
  date: String
) {
  Column(
    modifier = modifier.padding(top = 12.dp, bottom = 2.dp)
  ) {
    NowPlayingTitle(streamTitle)

    val typography = MaterialTheme.typography
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
    val string = buildAnnotatedString {
      withStyle(
        style = typography.bodySmall.copy(color = color).toSpanStyle()
      ) {
        append("$views  $date")
      }

      append(" ")

      withStyle(
        style = typography.bodySmall.copy(fontWeight = FontWeight.Bold)
          .toSpanStyle()
      ) {
        append("...more")
      }
    }
    Text(text = string)
  }
}

@Composable
internal fun DescriptionSheet(
  description: UIState,
  contentHeight: Dp,
  isSheetOpen: Boolean,
  toggleExpansion: () -> Unit
) {
  Scaffold(
    modifier = Modifier.heightIn(max = contentHeight),
    topBar = {
      SheetHeader(
        modifier = Modifier
          .padding(start = 16.dp)
          .testTag("watch:description_top_bar"),
        headerTitle = "Description",
        isSheetOpen = isSheetOpen,
        closeSheet = { dispatch(v("stream_panel_fsm", "close_desc_sheet")) },
        toggleExpansion = toggleExpansion
      )
    }
  ) { padding ->
    val descData = description.data
    val typography = MaterialTheme.typography
    val bodySmall = typography.bodySmall
    val colorScheme = MaterialTheme.colorScheme
    LazyColumn(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize()
        .nestedScroll(BottomSheetNestedScrollConnection())
        .testTag("watch:description_list")
    ) {
      item {
        Surface(
          modifier = Modifier.padding(
            horizontal = 12.dp,
            vertical = 16.dp
          )
        ) {
          NowPlayingTitle(
            streamTitle = get<String>(descData, Stream.title)!!,
            maxLines = Int.MAX_VALUE
          )
        }
      }

      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NowPlayingTitle(get<String>(descData, Stream.likes_count)!!)
            Text(
              text = "Likes",
              style = bodySmall.copy(colorScheme.onSurface.copy(alpha = .6f))
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NowPlayingTitle(get<String>(descData, Stream.views_full)!!)
            Text(
              text = "Views",
              style = bodySmall.copy(colorScheme.onSurface.copy(alpha = .6f))
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NowPlayingTitle(get<String>(descData, Stream.month_day)!!)
            Text(
              text = get<String>(descData, Stream.year)!!,
              style = bodySmall.copy(colorScheme.onSurface.copy(alpha = .6f))
            )
          }
        }
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }

      item {
        Divider(
          modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
        )
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }

      item {
        Html(text = get(descData, Stream.description)!!)
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }

      item {
        Divider(
          modifier = Modifier.fillMaxWidth(),
          thickness = 8.dp
        )
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }

      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { /* todo: */ }
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AuthorAvatar(
            url = get(descData, Stream.avatar)!!,
            size = 56.dp
          )

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = get<String>(descData, Stream.channel_name)!!,
              style = typography.titleMedium
            )

            Text(
              text = get<String>(
                descData,
                Stream.sub_count
              )!! + " subscribers",
              style = typography.bodyMedium.copy(
                color = colorScheme.onSurface.copy(alpha = .6f)
              )
            )
          }
        }
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DescriptionBottomSheetScaffold(
  modifier: Modifier = Modifier,
  description: UIState?,
  sheetUiState: UIState,
  isStreamLoading: Boolean,
  onClickSheetHeader: () -> Unit,
  content: @Composable (PaddingValues) -> Unit
) {
  val descBottomSheetState = rememberStandardBottomSheetState(
    initialValue = SheetValue.Hidden,
    skipHiddenState = false
  )
  val descScaffoldState = rememberBottomSheetScaffoldState(descBottomSheetState)
  SheetHiddenStateSyncEffect(
    sheetState = descBottomSheetState,
    onHiddenState = {
      dispatch(v("stream_panel_fsm", "close_desc_sheet"))
    }
  )
  val coroutineScope = rememberCoroutineScope()
  val descSheetData = sheetUiState.data
  BottomSheetScaffold(
    sheetContent = {
      RegFx(id = "expand_desc_sheet", coroutineScope, sheetUiState) {
        coroutineScope.launch { descBottomSheetState.expand() }
      }

      RegFx(id = "half_expand_desc_sheet", coroutineScope, sheetUiState) {
        coroutineScope.launch { descBottomSheetState.partialExpand() }
      }

      RegFx(id = "close_desc_sheet", coroutineScope, sheetUiState) {
        coroutineScope.launch { descBottomSheetState.hide() }
      }

      if (isStreamLoading) return@BottomSheetScaffold
      if (description == null) return@BottomSheetScaffold

      DescriptionSheet(
        description = description,
        contentHeight = get<Dp>(descSheetData, ":sheet_content_height")!!,
        isSheetOpen = get(descSheetData, ":is_desc_sheet_open", false)!!,
        toggleExpansion = onClickSheetHeader
      )
    },
    modifier = modifier.fillMaxSize(),
    scaffoldState = descScaffoldState,
    sheetPeekHeight = get<Dp>(descSheetData, ":sheet_peak_height")!!,
    sheetDragHandle = {
      DragHandle(onClick = onClickSheetHeader)
    },
    content = content
  )
}

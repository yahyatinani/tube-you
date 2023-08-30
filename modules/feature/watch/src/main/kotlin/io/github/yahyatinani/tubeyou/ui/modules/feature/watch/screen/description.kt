package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AuthorAvatar
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.StreamState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs.Stream
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DescriptionSheet(
  descSheetState: SheetState,
  sheetPeekHeight: Dp,
  uiState: UIState
) {
  Scaffold(
    modifier = Modifier
      .then(
        if (descSheetState.currentValue == SheetValue.PartiallyExpanded) {
          Modifier.height(sheetPeekHeight)
        } else {
          Modifier
        }
      ),
    topBar = {
      SheetHeader(
        modifier = Modifier
          .padding(start = 16.dp),
        headerTitle = "Description",
        sheetState = descSheetState.currentValue,
        closeSheet = { dispatch(v("stream_panel_fsm", "close_desc_sheet")) }
      )
    }
  ) { padding ->
    val sheetData = uiState.data
    val typography = MaterialTheme.typography
    val bodySmall = typography.bodySmall
    val colorScheme = MaterialTheme.colorScheme

    val streamState = get<StreamState>(sheetData, common.state)
    if (streamState == null || streamState == StreamState.LOADING) {
      return@Scaffold
    }

    LazyColumn(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize()
        .nestedScroll(BottomSheetNestedScrollConnection())
    ) {
      item {
        Surface(
          modifier = Modifier.padding(
            horizontal = 12.dp,
            vertical = 16.dp
          )
        ) {
          NowPlayingTitle(
            get<String>(sheetData, Stream.title)!!,
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
            NowPlayingTitle(get<String>(sheetData, Stream.likes_count)!!)
            Text(
              text = "Likes",
              style = bodySmall.copy(colorScheme.onSurface.copy(alpha = .6f))
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NowPlayingTitle(get<String>(sheetData, Stream.views_full)!!)
            Text(
              text = "Views",
              style = bodySmall.copy(colorScheme.onSurface.copy(alpha = .6f))
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NowPlayingTitle(get<String>(sheetData, Stream.month_day)!!)
            Text(
              text = get<String>(sheetData, Stream.year)!!,
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
        Html(text = get(sheetData, Stream.description)!!)
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
            url = get(sheetData, Stream.avatar)!!,
            size = 56.dp
          )

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = get<String>(sheetData, Stream.channel_name)!!,
              style = typography.titleMedium
            )

            Text(
              text = get<String>(
                sheetData,
                Stream.sub_count
              )!! + " subscribers",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
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

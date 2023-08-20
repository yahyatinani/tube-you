package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen

import android.text.Spanned
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.PartiallyExpanded
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AuthorAvatar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AvatarLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.component.CountText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.StreamLoaderPortrait
import com.github.yahyatinani.tubeyou.modules.designsystem.component.SubscribeButton
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TextLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyIconRoundedButton
import io.github.yahyatinani.recompose.RegFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.core.ui.ChannelItem
import io.github.yahyatinani.tubeyou.core.ui.PlayListPortrait
import io.github.yahyatinani.tubeyou.core.ui.VideoItemPortrait
import io.github.yahyatinani.tubeyou.core.viewmodels.ChannelVm
import io.github.yahyatinani.tubeyou.core.viewmodels.PlaylistVm
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.modules.feature.watch.R
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.ListState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.StreamState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs.Stream
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch

@Composable
fun NowPlayingTitle(streamTitle: String, maxLines: Int = 2) {
  Text(
    text = streamTitle,
    style = MaterialTheme.typography.titleMedium,
    maxLines = maxLines,
    overflow = TextOverflow.Ellipsis
  )
}

@Composable
fun ChannelSection(
  modifier: Modifier = Modifier,
  channelName: String,
  channelAvatar: String,
  subscribersCount: String
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    val typography = MaterialTheme.typography

    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically
    ) {
      AuthorAvatar(
        url = channelAvatar,
        size = 32.dp
      )

      Spacer(modifier = Modifier.width(12.dp))

      Text(
        modifier = Modifier.weight(1f, false),
        text = channelName,
        style = typography.bodyMedium.copy(fontWeight = FontWeight.W500),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )

      Spacer(modifier = Modifier.width(6.dp))

      CountText(subscribersCount)
    }

    Spacer(modifier = Modifier.width(16.dp))

    val colorScheme = MaterialTheme.colorScheme

    SubscribeButton(
      text = stringResource(R.string.subscribe),
      containerColor = colorScheme.onSurface,
      contentColor = colorScheme.surface
    ) {
      // TODO:
    }
  }
}

@Composable
fun LikeSection(
  modifier: Modifier = Modifier,
  likesCount: String,
  buttonsColor: Color
) {
  val size = 18.dp
  val textStyle = MaterialTheme.typography.labelMedium

  Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
    val horizontal = 10.dp
    val vertical = 7.dp
    Surface(
      color = buttonsColor,
      shape = RoundedCornerShape(20.dp)
    ) {
      Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        TyIconRoundedButton(
          text = likesCount,
          textStyle = textStyle,
          containerColor = Color.Transparent,
          onClick = { /* todo: */ },
          horizontal = horizontal,
          vertical = vertical
        ) {
          Icon(
            modifier = Modifier.size(size),
            imageVector = Icons.Outlined.ThumbUp,
            contentDescription = ""
          )
        }

        Divider(
          modifier = Modifier
            .width(1.dp)
            .fillMaxHeight(),
          color = MaterialTheme.colorScheme.onBackground.copy(.1f)
        )

        TyIconRoundedButton(
          text = "-1",
          textStyle = textStyle,
          containerColor = Color.Transparent,
          onClick = { /* todo: */ },
          horizontal = horizontal,
          vertical = vertical
        ) {
          Icon(
            modifier = Modifier.size(size),
            imageVector = Icons.Outlined.ThumbDown,
            contentDescription = ""
          )
        }
      }
    }

    Spacer(modifier = Modifier.width(16.dp))

    TyIconRoundedButton(
      text = "Share",
      textStyle = textStyle,
      containerColor = buttonsColor,
      onClick = { /* todo: */ },
      horizontal = horizontal,
      vertical = vertical
    ) {
      Icon(
        modifier = Modifier.size(size),
        imageVector = Icons.Outlined.Share,
        contentDescription = ""
      )
    }

    Spacer(modifier = Modifier.width(16.dp))

    TyIconRoundedButton(
      text = "Download",
      textStyle = textStyle,
      containerColor = buttonsColor,
      onClick = { /* todo: */ },
      horizontal = horizontal,
      vertical = vertical
    ) {
      Icon(
        modifier = Modifier.size(size),
        imageVector = Icons.Outlined.Download,
        contentDescription = ""
      )
    }

    Spacer(modifier = Modifier.width(16.dp))

    TyIconRoundedButton(
      text = "Save",
      textStyle = textStyle,
      containerColor = buttonsColor,
      onClick = { /* todo: */ },
      horizontal = horizontal,
      vertical = vertical
    ) {
      Icon(
        modifier = Modifier.size(size),
        imageVector = Icons.Outlined.LibraryAdd,
        contentDescription = ""
      )
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NowPlayingSheet(
  modifier: Modifier = Modifier,
  isCollapsed: Boolean,
  onCollapsedClick: () -> Unit,
  activeStream: UIState,
  activeStreamCache: VideoVm,
  showThumbnail: Boolean?,
  sheetPeekHeight: Dp,
  onClickClosePlayer: () -> Unit = { }
) {
  val descSheetState = rememberStandardBottomSheetState(
    initialValue = SheetValue.Hidden,
    skipHiddenState = false
  )
  val descScaffoldState = rememberBottomSheetScaffoldState(descSheetState)
  val descriptionSheetState = watch<SheetValue>(query = v("description_sheet"))
  val descSheetValue = descSheetState.currentValue
  val descTargetValue = descSheetState.targetValue
  val descSheetPeekHeight =
    remember(descriptionSheetState, descTargetValue, descSheetValue) {
      if (descriptionSheetState == SheetValue.Hidden) {
        0.dp
      } else if (
        descSheetValue == SheetValue.Hidden &&
        descTargetValue == SheetValue.Hidden
      ) {
        dispatch(v("stream_panel_fsm", "close_desc_sheet"))
        0.dp
      } else {
        sheetPeekHeight
      }
    }

  val playbackScope = rememberCoroutineScope()
  RegFx(id = "half_expand_desc_sheet", playbackScope, descSheetState) {
    playbackScope.launch { descSheetState.partialExpand() }
  }

  RegFx(id = "close_desc_sheet", playbackScope, descSheetState) {
    playbackScope.launch { descSheetState.hide() }
  }

  val streamData = activeStream.data
  val streamState = remember(activeStream) {
    get<StreamState>(streamData, common.state)
  }
  val isLoading = remember(streamState) {
    streamState == null || streamState == StreamState.LOADING
  }
  val height = remember(sheetPeekHeight) { sheetPeekHeight - 18.dp }
  BottomSheetScaffold(
    modifier = modifier,
    scaffoldState = descScaffoldState,
    sheetPeekHeight = descSheetPeekHeight,
    sheetDragHandle = {
      DragHandle {
        playbackScope.launch {
          if (descSheetValue == PartiallyExpanded) {
            descSheetState.expand()
          } else if (descSheetValue == SheetValue.Expanded) {
            descSheetState.partialExpand()
          }
        }
      }
    },
    sheetContent = {
      DescriptionSheet(
        descSheetState = descSheetState,
        sheetPeekHeight = height,
        uiState = activeStream
      )
    }
  ) {
    val commentsSheetState = rememberStandardBottomSheetState(
      initialValue = SheetValue.Hidden,
      skipHiddenState = false
    )
    val commentsScaffoldState = rememberBottomSheetScaffoldState(
      bottomSheetState = commentsSheetState
    )
    val commentsSheetValue = watch<SheetValue>(query = v("comments_sheet"))
    val commentsTargetValue = commentsSheetState.targetValue
    val commentsCurrentValue = commentsSheetState.currentValue
    val commentsSheetPeekHeight =
      remember(commentsSheetValue, commentsTargetValue, commentsCurrentValue) {
        when {
          commentsSheetValue == SheetValue.Hidden -> 0.dp
          commentsTargetValue == SheetValue.Hidden &&
            commentsCurrentValue == SheetValue.Hidden -> {
            dispatch(v("stream_panel_fsm", "close_comments_sheet"))
            0.dp
          }

          else -> sheetPeekHeight
        }
      }

    RegFx(
      id = "half_expand_comments_sheet",
      playbackScope,
      commentsSheetState
    ) {
      playbackScope.launch { commentsSheetState.partialExpand() }
    }
    RegFx(
      id = "close_comments_sheet",
      playbackScope,
      commentsSheetState
    ) {
      playbackScope.launch {
        commentsSheetState.hide()
        dispatch(v("nav_back_to_comments"))
      }
    }

    val commentsPanelState = watch<UIState>(query = v("comments_panel"))
    BottomSheetScaffold(
      scaffoldState = commentsScaffoldState,
      sheetPeekHeight = commentsSheetPeekHeight,
      sheetDragHandle = {
        DragHandle {
          playbackScope.launch {
            if (commentsSheetState.currentValue == PartiallyExpanded) {
              commentsSheetState.expand()
            } else if (commentsSheetState.currentValue == SheetValue.Expanded) {
              commentsSheetState.partialExpand()
            }
          }
        }
      },
      sheetContent = {
        CommentsSheet(
          commentsSheetState = commentsSheetState,
          sheetPeekHeight = height,
          uiState = commentsPanelState
        )
      }
    ) {
      val colorScheme = MaterialTheme.colorScheme
      val containerColor: Color = colorScheme.primaryContainer

      Column {
        val typography = MaterialTheme.typography
        val bodySmall = typography.bodySmall
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable(
              enabled = isCollapsed,
              indication = null,
              interactionSource = remember { MutableInteractionSource() },
              onClick = onCollapsedClick
            ),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          val ratio = remember { 16 / 9f }
          Row(modifier = Modifier.weight(.8f)) {
            VideoPlayer(
              modifier = Modifier
                .background(color = Color.Black)
                .then(
                  if (isCollapsed) {
                    Modifier
                      .height(110.dp - 54.dp)
                      .width(136.dp)
//                      .aspectRatio(ratio)
                  } else {
                    Modifier
                      .fillMaxWidth()
                      .aspectRatio(
                        get(streamData, Stream.aspect_ratio, ratio)!!
                      )
                  }
                ),
              streamState = activeStream,
              useController = !isCollapsed,
              showThumbnail = showThumbnail,
              thumbnail = activeStreamCache.thumbnail
            )

            if (isCollapsed) {
              if (!isLoading) {
                Column(Modifier.padding(8.dp)) {
                  Text(
                    text = get<String>(streamData, Stream.title)!!,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = bodySmall
                  )
                  Text(
                    text = get<String>(streamData, Stream.uploader)!!,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = bodySmall.copy(
                      color = colorScheme.onSurface.copy(alpha = .6f)
                    )
                  )
                }
              }
            }
          }
          if (isCollapsed) {
            val playerState = get<StreamState>(activeStream.data, common.state)
            MiniPlayerControls(
              modifier = Modifier.weight(.2f),
              isPlaying = playerState == StreamState.PLAYING,
              onClickClose = onClickClosePlayer
            ) {
              dispatch(v("stream_panel_fsm", "toggle_play_pause"))
            }
          }
        }

        val lazyListState = rememberLazyListState()
        LaunchedEffect(key1 = isLoading) {
          lazyListState.scrollToItem(0)
        }

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .nestedScroll(BlockScrolling),
          state = lazyListState
        ) {
          item {
            if (isLoading) {
              Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                  modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  color = containerColor
                ) {
                  Column(
                    modifier = Modifier.padding(
                      horizontal = 6.dp,
                      vertical = 4.dp
                    )
                  ) {
                    NowPlayingTitle(streamTitle = activeStreamCache.title)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = activeStreamCache.viewCount,
                      style = bodySmall.copy(
                        colorScheme.onSurface.copy(alpha = .6f)
                      )
                    )
                  }
                }

                val roundedCornerShape = RoundedCornerShape(20.dp)
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarLoader(
                      modifier = Modifier.size(size = 32.dp),
                      containerColor = containerColor
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    TextLoader(
                      modifier = Modifier.width(128.dp),
                      containerColor = containerColor
                    )
                  }

                  Surface(
                    modifier = Modifier
                      .width(72.dp)
                      .height(32.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = containerColor,
                    content = {}
                  )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                  modifier = Modifier
                    .horizontalScroll(rememberScrollState(), enabled = false)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                ) {
                  Surface(
                    modifier = Modifier
                      .width(128.dp)
                      .height(30.dp),
                    shape = roundedCornerShape,
                    color = containerColor,
                    content = {}
                  )

                  repeat(3) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Surface(
                      modifier = Modifier
                        .width(80.dp)
                        .height(30.dp),
                      shape = roundedCornerShape,
                      color = containerColor,
                      content = {}
                    )
                  }
                }
              }

              return@item
            }

            Column {
              DescriptionSection(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    dispatch(
                      v(
                        "stream_panel_fsm",
                        "half_expand_desc_sheet"
                      )
                    )
                  }
                  .padding(horizontal = 12.dp),
                streamTitle = get<String>(streamData, Stream.title)!!,
                views = get<String>(streamData, Stream.views)!!,
                date = get(streamData, Stream.date)!!
              )

              ChannelSection(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { }
                  .padding(horizontal = 12.dp),
                channelName = get(streamData, Stream.channel_name)!!,
                channelAvatar = get(streamData, Stream.avatar)!!,
                subscribersCount = get(streamData, Stream.sub_count)!!
              )

              Spacer(modifier = Modifier.height(8.dp))

              LikeSection(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp),
                likesCount = get<String>(streamData, Stream.likes_count)!!,
                buttonsColor = containerColor
              )
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          val commentsStateData =
            get<UIState>(commentsPanelState.data, "comments")!!.data
              as IPersistentMap<Any?, Any?>
          item {
            if (
              get<ListState>(
                commentsStateData,
                common.state
              ) == ListState.LOADING
            ) {
              Surface(
                modifier = Modifier
                  .padding(horizontal = 12.dp)
                  .fillMaxWidth()
                  .height(88.dp),
                color = containerColor,
                shape = RoundedCornerShape(12.dp),
                content = { }
              )

              return@item
            }

            val commentsSection = get<IPersistentMap<Any, Any>>(
              commentsStateData,
              "comments_section"
            )
            val highlightedComment =
              get<Spanned>(commentsSection, Stream.highlight_comment)
            val commentAvatar =
              get<String>(commentsSection, Stream.highlight_comment_avatar)
            val commentsCount = (
              get<String>(commentsSection, Stream.comments_count)
                ?: ""
              )
            val commentsDisabled =
              get<Boolean>(commentsSection, Stream.comments_disabled) ?: false

            CommentsSection(
              modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
              highlightedComment = highlightedComment,
              containerColor = containerColor,
              commentsCount = commentsCount,
              commentAvatar = commentAvatar,
              commentsDisabled = commentsDisabled,
              onClick = {
                dispatch(v("stream_panel_fsm", "half_expand_comments_sheet"))
              }
            )
          }

          item { Spacer(modifier = Modifier.height(24.dp)) }

          if (isLoading) {
            repeat(2) {
              item {
                StreamLoaderPortrait(containerColor)
              }
            }

            return@LazyColumn
          }

          val videoInfoTextStyle = TextStyle.Default.copy(fontSize = 14.sp)
          items(
            get<List<Any>>(streamData, Stream.related_streams)!!
          ) { viewModel ->
            when (viewModel) {
              is VideoVm -> {
                VideoItemPortrait(
                  viewModel = viewModel,
                  videoInfoTextStyle = videoInfoTextStyle,
                  onClick = {
                    dispatch(
                      v("stream_panel_fsm", common.play_video, viewModel)
                    )
                  }
                )
              }

              is ChannelVm -> {
                ChannelItem(
                  modifier = Modifier
                    .clickable { /*TODO*/ }
                    .fillMaxWidth(),
                  vm = viewModel
                )
              }

              else -> PlayListPortrait(
                modifier = Modifier.padding(start = 12.dp),
                viewModel = viewModel as PlaylistVm,
                videoInfoTextStyle = videoInfoTextStyle
              )
            }

            Spacer(modifier = Modifier.height(8.dp))
          }
        }
      }
    }
  }
}

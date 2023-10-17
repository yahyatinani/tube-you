package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen

import android.text.Spanned
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
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
import com.github.yahyatinani.tubeyou.modules.designsystem.component.BOTTOM_BAR_HEIGHT
import com.github.yahyatinani.tubeyou.modules.designsystem.component.CountText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.StreamLoaderPortrait
import com.github.yahyatinani.tubeyou.modules.designsystem.component.SubscribeButton
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TextLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyIconRoundedButton
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.tubeyou.core.ui.ChannelItem
import io.github.yahyatinani.tubeyou.core.ui.PlayListPortrait
import io.github.yahyatinani.tubeyou.core.ui.VideoItemPortrait
import io.github.yahyatinani.tubeyou.core.viewmodels.ChannelVm
import io.github.yahyatinani.tubeyou.core.viewmodels.PlaylistVm
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.modules.feature.watch.R
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.StreamState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fx.RegPlayerSheetEffects
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs.Stream
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.math.abs
import kotlin.math.roundToInt

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
            imageVector = TyIcons.ThumbUpOutlined,
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
            imageVector = TyIcons.ThumbDownOutlined,
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
        imageVector = TyIcons.Share,
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
        modifier = Modifier.size(18.dp),
        imageVector = TyIcons.Download,
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
        imageVector = TyIcons.LibraryAdd,
        contentDescription = ""
      )
    }
  }
}

fun lerp(
  sheetYOffset: Float,
  traverse: Float,
  start: Float = 0f,
  end: Float = 1f
): Float {
  val d = (1f - abs(sheetYOffset / traverse)) * (start - end) + end
  return d.coerceIn(minOf(start, end), maxOf(start, end))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetHiddenStateSyncEffect(
  sheetState: SheetState,
  onHiddenState: () -> Unit
) {
  LaunchedEffect(sheetState) {
    snapshotFlow { sheetState.currentValue }
      .map { it == Hidden }
      .distinctUntilChanged()
      .filter { it }
      .collect {
        onHiddenState()
      }
  }
}

@Composable
fun NowPlayingBottomSheet(
  modifier: Modifier = Modifier,
  nowPlayingStream: UIState,
  isPlayerSheetMinimized: Boolean,
  sheetOffset: () -> Float,
  onClickCloseSheet: () -> Unit,
  onClickMiniPlayer: () -> Unit
) {
  Surface(modifier = modifier) {
    val streamData = nowPlayingStream.data
    val isStreamLoading = get<Boolean>(streamData, common.is_loading)!!
    val density = LocalDensity.current
    DescriptionBottomSheetScaffold(
      description = watch(v("description_state")),
      sheetUiState = watch(
        v("description_sheet_state", LocalConfiguration.current, density)
      ),
      isStreamLoading = isStreamLoading,
      onClickSheetHeader = {
        dispatch(v("stream_panel_fsm", "toggle_desc_expansion"))
      }
    ) {
      val commentsPanelState = watch<UIState?>(query = v("comments_panel"))

      CommentsBottomSheetScaffold(
        commentsListState = commentsPanelState,
        sheetUiState = watch(
          v("comments_sheet_state", LocalConfiguration.current, density)
        ),
        onClickSheetHeader = {
          dispatch(v("stream_panel_fsm", "toggle_comments_expansion"))
        }
      ) {
        val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
        val screenHeightPx = remember(density, screenHeightDp) {
          with(density) { screenHeightDp.toPx() }
        }
        val colorScheme = MaterialTheme.colorScheme
        val containerColor: Color = colorScheme.primaryContainer
        Column {
          val bodySmall = MaterialTheme.typography.bodySmall

          val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
          val screenWidthPx = remember(density, screenWidthDp) {
            with(density) { screenWidthDp.toPx() }
          }
          val miniPlayerHeightPx = remember(density) {
            with(density) { MINI_PLAYER_HEIGHT.toPx() }
          }
          val streamMaxHeightDp = get<Dp>(streamData, "stream_height")!!
          val sheetOffsetPx by remember { derivedStateOf { sheetOffset() } }

          val bottomBarMiniPlayer = remember(density) {
            with(density) { 104.dp.toPx() }
          }

          dispatch(
            v<Any>(
              "set_volume",
              sheetOffsetPx,
              screenHeightPx,
              bottomBarMiniPlayer,
              miniPlayerHeightPx
            )
          )

          val streamMaxHeightPx = remember(streamMaxHeightDp) {
            with(density) { streamMaxHeightDp.toPx() }
          }
          val streamHalfHeight =
            remember(streamMaxHeightPx) { streamMaxHeightPx / 2 }

          val bottomBar =
            remember { with(density) { BOTTOM_BAR_HEIGHT.toPx() } }
          val shrinkPoint = remember(screenHeightPx, streamHalfHeight) {
            screenHeightPx - (bottomBar + streamHalfHeight)
          }
          val minPlayerWidthPx =
            remember { with(density) { MINI_PLAYER_WIDTH.toPx() } }

          val streamTitle = get<String>(streamData, Stream.title)!!
          Layout(
            modifier = modifier
              .heightIn(
                min = MINI_PLAYER_HEIGHT,
                max = streamMaxHeightDp
              ),
            content = {
              val isSheetMoving by remember {
                derivedStateOf { sheetOffset() > 0f }
              }
              VideoPlayer(
                modifier = Modifier
                  .testTag("watch:video_player")
                  .background(color = Color.Black),
                streamState = nowPlayingStream,
                useController = !isPlayerSheetMinimized && !isSheetMoving
              )

              Column(
                modifier = Modifier
                  .padding(8.dp)
                  .clickable(
                    enabled = isPlayerSheetMinimized,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClickMiniPlayer
                  )
              ) {
                if (!isStreamLoading) {
                  Text(
                    text = streamTitle,
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

              MiniPlayerControls(
                modifier = Modifier.testTag("watch:mini_player_controls"),
                isPlaying = get<StreamState>(
                  streamData,
                  common.state
                ) == StreamState.PLAYING,
                onClickClose = onClickCloseSheet,
                onClickPlay = {
                  dispatch(v("stream_panel_fsm", "toggle_play_pause"))
                }
              )

              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .drawWithContent {
                    drawContent()
                    val x = (MINI_PLAYER_HEIGHT + BOTTOM_BAR_HEIGHT).toPx()
                    drawRect(
                      color = Color.Black,
                      alpha = lerp(
                        sheetYOffset = sheetOffsetPx - shrinkPoint,
                        traverse = screenHeightPx - (shrinkPoint + x),
                        start = 1f,
                        end = 0f
                      )
                    )
                  }
              )
            }
          ) { measurables, constraints ->
            val (videoWidth, layoutH) = when {
              sheetOffsetPx < shrinkPoint -> {
                screenWidthPx to lerp(
                  sheetYOffset = sheetOffsetPx,
                  traverse = screenHeightPx - (streamHalfHeight + bottomBar),
                  start = streamMaxHeightPx,
                  end = streamHalfHeight
                )
              }

              else -> {
                lerp(
                  sheetYOffset = sheetOffsetPx - shrinkPoint,
                  traverse = streamHalfHeight - miniPlayerHeightPx,
                  start = screenWidthPx,
                  end = minPlayerWidthPx
                ) to lerp(
                  sheetYOffset = sheetOffsetPx - shrinkPoint,
                  traverse = streamHalfHeight - miniPlayerHeightPx,
                  start = streamHalfHeight,
                  end = miniPlayerHeightPx
                )
              }
            }

            val layoutHeight = layoutH.roundToInt()
            val videoWidthInt = videoWidth.roundToInt()

            val videoHeight = (videoWidth * streamMaxHeightPx) / screenWidthPx

            val videoPlaceable = measurables[0].measure(
              constraints.copy(
                maxWidth = videoWidthInt,
                minHeight = layoutHeight,
                maxHeight = videoHeight.roundToInt()
              )
            )

            layout(width = screenWidthPx.roundToInt(), height = layoutHeight) {
              val y = (layoutHeight - videoPlaceable.height) / 2

              videoPlaceable.placeRelative(x = 0, y = y)

              if (videoWidth == screenWidthPx) {
                return@layout
              }

              val ctrlButtons = measurables[2].measure(
                constraints.copy(
                  maxWidth = (screenWidthPx - videoWidth).roundToInt()
                )
              )

              val ctrlButtonsFinalX =
                (screenWidthPx - ctrlButtons.width).roundToInt()
              val ctrlButtonsX = videoWidthInt.coerceAtLeast(ctrlButtonsFinalX)

              val title = measurables[1].measure(
                constraints.copy(
                  maxWidth = (ctrlButtonsX - videoWidth).roundToInt()
                )
              )
              val mask = measurables[3].measure(constraints)

              ctrlButtons.placeRelative(x = ctrlButtonsX, y = 0)
              title.placeRelative(x = videoWidthInt, y = 0)
              mask.placeRelative(x = videoWidthInt, y = 0)
            }
          }

          val lazyListState = rememberLazyListState()
          LaunchedEffect(key1 = isStreamLoading) {
            lazyListState.scrollToItem(0)
          }

          val traverse = remember(screenHeightPx, density) {
            with(density) { screenHeightPx - 160.dp.toPx() }
          }
          Surface {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .nestedScroll(BlockScrolling)
                .testTag("watch:related_streams")
                .drawWithContent {
                  drawContent()
                  drawRect(
                    color = Color.Black,
                    alpha = lerp(
                      sheetYOffset = sheetOffsetPx,
                      traverse = traverse
                    )
                  )
                },
              state = lazyListState
            ) {
              item {
                if (isStreamLoading) {
                  Column(modifier = Modifier.fillMaxSize()) {
                    Surface(
                      modifier = Modifier
                        .testTag("watch:content_loader")
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
                        NowPlayingTitle(streamTitle = streamTitle)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = get<String>(streamData, Stream.views)!!,
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
                        .horizontalScroll(
                          rememberScrollState(),
                          enabled = false
                        )
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
                      .padding(horizontal = 12.dp)
                      .testTag("watch:description_section"),
                    streamTitle = streamTitle,
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
                    likesCount = get<String>(
                      streamData,
                      Stream.likes_count
                    )!!,
                    buttonsColor = containerColor
                  )
                }
              }

              item { Spacer(modifier = Modifier.height(16.dp)) }

              item {
                if (
                  commentsPanelState != null &&
                  get(commentsPanelState.data, "is_loading", false)!!
                ) {
                  Surface(
                    modifier = Modifier
                      .testTag("watch:comments_section_loader")
                      .padding(horizontal = 12.dp)
                      .fillMaxWidth()
                      .height(88.dp),
                    color = containerColor,
                    shape = RoundedCornerShape(12.dp),
                    content = { }
                  )

                  return@item
                }

                val commentsSection =
                  watch<Any>(query = v(":comments_section"))

                CommentsSection(
                  modifier = Modifier
                    .testTag("watch:comments_section")
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                  highlightedComment = get<Spanned>(
                    commentsSection,
                    Stream.highlight_comment
                  ),
                  containerColor = containerColor,
                  commentsCount = get<String>(
                    commentsSection,
                    Stream.comments_count
                  ) ?: "",
                  commentAvatar = get<String>(
                    commentsSection,
                    Stream.highlight_comment_avatar
                  ),
                  commentsDisabled = get<Boolean>(
                    commentsSection,
                    Stream.comments_disabled
                  ) ?: false,
                  onClick = {
                    dispatch(
                      v("stream_panel_fsm", "half_expand_comments_sheet")
                    )
                  }
                )
              }

              item { Spacer(modifier = Modifier.height(24.dp)) }

              if (isStreamLoading) {
                repeat(2) {
                  item {
                    StreamLoaderPortrait(containerColor)
                  }
                }

                return@LazyColumn
              }

              val videoInfoTextStyle =
                TextStyle.Default.copy(fontSize = 14.sp)
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
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NowPlayingScaffold(
  modifier: Modifier = Modifier,
  nowPlayingStream: UIState?,
  scaffoldState: BottomSheetScaffoldState,
  bottomSheetOffset: () -> Float,
  content: @Composable (PaddingValues) -> Unit
) {
  BottomSheetScaffold(
    sheetContent = {
      RegPlayerSheetEffects(scaffoldState.bottomSheetState)

      if (nowPlayingStream == null) return@BottomSheetScaffold

      NowPlayingBottomSheet(
        nowPlayingStream = nowPlayingStream,
        isPlayerSheetMinimized = watch(query = v("is_player_sheet_minimized")),
        sheetOffset = bottomSheetOffset,
        onClickCloseSheet = {
//          dispatch(v("stream_panel_fsm", common.close_player))
          dispatch(v(common.close_player))
        },
        onClickMiniPlayer = {
          dispatch(v<Any>("stream_panel_fsm", common.expand_player_sheet))
        }
      )
    },
    modifier = modifier,
    scaffoldState = scaffoldState,
    sheetPeekHeight = remember(nowPlayingStream != null) {
      when (nowPlayingStream) {
        null -> 0.dp
        else -> MINI_PLAYER_HEIGHT + BOTTOM_BAR_HEIGHT
      }
    },
    sheetDragHandle = null,
    sheetShape = RoundedCornerShape,
    content = content
  )
}

val RoundedCornerShape = RoundedCornerShape(0.dp)

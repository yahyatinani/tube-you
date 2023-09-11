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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons
import io.github.yahyatinani.recompose.RegFx
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
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.ListState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.StreamState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs.Stream
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch
import kotlin.math.abs

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
  sheetOffset: () -> Float,
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
  val toggleDescExpansion: () -> Unit = {
    playbackScope.launch {
      if (descSheetState.currentValue == PartiallyExpanded) {
        descSheetState.expand()
      } else if (descSheetState.currentValue == SheetValue.Expanded) {
        descSheetState.partialExpand()
      }
    }
  }
  BottomSheetScaffold(
    sheetContent = {
      DescriptionSheet(
        descSheetState = descSheetState,
        sheetPeekHeight = height,
        uiState = activeStream,
        toggleExpansion = toggleDescExpansion
      )
    },
    modifier = modifier,
    scaffoldState = descScaffoldState,
    sheetPeekHeight = descSheetPeekHeight,
    sheetDragHandle = {
      DragHandle(onClick = toggleDescExpansion)
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

    val toggleCommentsExpansion: () -> Unit = {
      playbackScope.launch {
        if (commentsSheetState.currentValue == PartiallyExpanded) {
          commentsSheetState.expand()
        } else if (commentsSheetState.currentValue == SheetValue.Expanded) {
          commentsSheetState.partialExpand()
        }
      }
    }
    val commentsPanelState = watch<UIState>(query = v("comments_panel"))
    BottomSheetScaffold(
      sheetContent = {
        CommentsSheet(
          sheetState = commentsSheetState,
          sheetPeekHeight = height,
          uiState = commentsPanelState,
          toggleExpansion = toggleCommentsExpansion
        )
      },
      scaffoldState = commentsScaffoldState,
      sheetPeekHeight = commentsSheetPeekHeight,
      sheetDragHandle = {
        DragHandle(onClick = toggleCommentsExpansion)
      }
    ) {
      val density = LocalDensity.current
      val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
      val screenHeightPx = remember(density, screenHeightDp) {
        with(density) { screenHeightDp.toPx() }
      }
      val colorScheme = MaterialTheme.colorScheme
      val containerColor: Color = colorScheme.primaryContainer
      Column {
        val typography = MaterialTheme.typography
        val bodySmall = typography.bodySmall

        val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
        val defaultRatio = remember { 16 / 9f }
        val miniPlayerHeightPx = remember(density) {
          with(density) { 56.dp.toPx() }
        }
        val ratio = get(streamData, Stream.aspect_ratio, defaultRatio)!!
        val fullVidHeight = with(density) {
          remember(screenWidthDp, ratio) { screenWidthDp.toPx() / ratio }
        }
        val sheetOffsetPx by remember { derivedStateOf { sheetOffset() } }

        val bottomBar = remember(density) {
          with(density) { 48.dp.toPx() + miniPlayerHeightPx }
        }
        val fullVideoHeightDp = remember(fullVidHeight) {
          with(density) { fullVidHeight.toDp() }
        }
        val h = remember(density, sheetOffsetPx, fullVidHeight) {
          with(density) {
            lerp(
              sheetYOffset = sheetOffsetPx,
              traverse = screenHeightPx - bottomBar,
              start = fullVidHeight,
              end = miniPlayerHeightPx
            ).toDp()
          }
        }

        val widthShrinkingY = remember(screenHeightPx) { screenHeightPx / 4 }
        val minVideoWidthPx = remember {
          with(density) { MINI_PLAYER_WIDTH.toPx() }
        }
        val breakingPoint =
          remember(widthShrinkingY) { screenHeightPx - widthShrinkingY }

        val w =
          remember(density, sheetOffsetPx, screenWidthDp, widthShrinkingY) {
            with(density) {
              if (sheetOffsetPx < breakingPoint) {
                screenWidthDp
              } else {
                lerp(
                  sheetYOffset = sheetOffsetPx - breakingPoint,
                  traverse = widthShrinkingY - bottomBar,
                  start = screenWidthDp.toPx(),
                  end = minVideoWidthPx
                ).toDp()
              }
            }
          }
        Row(
          modifier = Modifier
            .widthIn(min = MINI_PLAYER_WIDTH)
            .heightIn(min = MINI_PLAYER_HEIGHT, max = fullVideoHeightDp)
            .height(h)
            .clickable(
              enabled = isCollapsed,
              indication = null,
              interactionSource = remember { MutableInteractionSource() },
              onClick = onCollapsedClick
            ),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          VideoPlayer(
            modifier = Modifier
              .width(w)
              .background(color = Color.Black),
            streamState = activeStream,
            useController = !isCollapsed,
            showThumbnail = showThumbnail,
            thumbnail = activeStreamCache.thumbnail
          )
          Box {
            val delta =
              remember(density) { with(density) { (56 + 48).dp.toPx() } }
            val traverse = remember(screenHeightPx, breakingPoint) {
              screenHeightPx - breakingPoint - delta
            }
            Row {
              Column(
                modifier = Modifier
                  .padding(8.dp)
                  .weight(.7f)
              ) {
                if (!isLoading) {
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

              if (sheetOffsetPx >= breakingPoint) {
                val weight = remember(sheetOffsetPx, traverse) {
                  lerp(
                    sheetYOffset = sheetOffsetPx - breakingPoint,
                    traverse = traverse,
                    start = .0001f,
                    end = .3f
                  )
                }

                val playerState =
                  get<StreamState>(activeStream.data, common.state)

                MiniPlayerControls(
                  modifier = Modifier.weight(weight),
                  isPlaying = playerState == StreamState.PLAYING,
                  onClickClose = onClickClosePlayer,
                  playPausePlayer = {
                    dispatch(v("stream_panel_fsm", "toggle_play_pause"))
                  }
                )
              }
            }

            val alpha = remember(sheetOffsetPx) {
              lerp(
                sheetYOffset = sheetOffsetPx - breakingPoint,
                traverse = traverse,
                start = 1f,
                end = 0f
              )
            }
            Box(
              modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                  drawRect(Color.Black.copy(alpha = alpha))
                }
            )
          }
        }

        val lazyListState = rememberLazyListState()
        LaunchedEffect(key1 = isLoading) {
          lazyListState.scrollToItem(0)
        }

        Box(modifier = Modifier.background(colorScheme.background)) {
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

          val traverse = remember(screenHeightPx, density) {
            with(density) { screenHeightPx - 160.dp.toPx() }
          }
          val alpha = remember(sheetOffsetPx) { lerp(sheetOffsetPx, traverse) }
          Box(
            modifier = Modifier
              .fillMaxSize()
              .drawBehind {
                drawRect(Color.Black.copy(alpha = alpha))
              }
          )
        }
      }
    }
  }
}

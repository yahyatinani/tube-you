package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Typeface
import android.text.Spanned
import android.text.TextUtils
import android.text.util.Linkify
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.SheetValue.PartiallyExpanded
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.text.HtmlCompat.fromHtml
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AppendingLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AuthorAvatar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.CountText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ExpandableText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.HeadedSheetColumn
import com.github.yahyatinani.tubeyou.modules.designsystem.component.SubscribeButton
import com.github.yahyatinani.tubeyou.modules.designsystem.component.Thumbnail
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyIconRoundedButton
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue300
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Grey300
import com.github.yahyatinani.tubeyou.modules.panel.common.AppendingPanelVm
import com.github.yahyatinani.tubeyou.modules.panel.common.Stream
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch

@Composable
fun QualityList(
  modifier: Modifier = Modifier,
  resolutions: List<Pair<String, Int>>,
  containerColor: Color
) {
  LazyColumn(modifier = modifier.padding(bottom = 16.dp)) {
    items(items = resolutions) { res ->
      ListItem(
        modifier = Modifier
          .clickable { dispatch(v("set_player_resolution", res.second)) }
          .padding(horizontal = 56.dp),
        headlineContent = {
          Text(text = res.first)
        },
        colors = ListItemDefaults.colors(containerColor = containerColor)
      )
    }
  }
}

@Composable
fun DragHandle(modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier
      .padding(top = 8.dp, bottom = 2.dp)
      .semantics { contentDescription = "dragHandleDescription" },
    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .2f),
    shape = MaterialTheme.shapes.extraLarge
  ) {
    Box(Modifier.size(width = 40.0.dp, height = 4.0.dp))
  }
}

private fun Context.findWindow(): Window? {
  var context = this
  while (context is ContextWrapper) {
    if (context is Activity) return context.window
    context = context.baseContext
  }
  return null
}

@Composable
fun MiniPlayerControls(
  isPlaying: Boolean,
  onClosePlayer: () -> Unit = { },
  playPausePlayer: () -> Unit = { }
) {
  with(LocalContext.current.findWindow()) {
    this?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
  }
  Row(
    modifier = Modifier.height(110.dp - 48.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val color = MaterialTheme.colorScheme.onBackground
    val colorFilter = ColorFilter.tint(color = color)
    IconButton(onClick = playPausePlayer) {
      val playerIcon = when (isPlaying) {
        true -> Icons.Default.Pause
        else -> Icons.Default.PlayArrow
      }
      Image(
        imageVector = playerIcon,
        contentDescription = "play/pause",
        colorFilter = colorFilter
      )
    }

    IconButton(onClick = onClosePlayer) {
      Image(
        imageVector = Icons.Default.Close,
        contentDescription = "close video",
        colorFilter = colorFilter
      )
    }
  }
}

@Composable
@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
fun VideoPlayer(
  modifier: Modifier = Modifier,
  streamData: IPersistentMap<Any, Any>?,
  useController: Boolean = true,
  isCollapsed: Boolean,
  playerState: PlayerState?,
  showThumbnail: Boolean?,
  thumbnail: String?
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val orientation = LocalConfiguration.current.orientation

    var showQualityControl: Boolean by remember { mutableStateOf(false) }
    var showQualitiesSheet: Boolean by remember { mutableStateOf(false) }

    val ratio by remember { mutableFloatStateOf(16 / 9f) }

    val controllerVisibilityListener = remember {
      PlayerView.ControllerVisibilityListener { visibility: Int ->
        showQualityControl = visibility == View.VISIBLE
      }
    }

    Box(
      modifier = Modifier
        .then(
          if (orientation != ORIENTATION_LANDSCAPE) {
            if (isCollapsed) {
              Modifier
                .height(110.dp - 48.dp)
                .aspectRatio(ratio)
            } else if (streamData != null) {
              Modifier
                .fillMaxWidth()
                .aspectRatio(get(streamData, Stream.aspect_ratio)!!)
            } else {
              Modifier
                .fillMaxWidth()
                .aspectRatio(ratio)
            }
          } else {
            Modifier
          }
        )
    ) {
      LaunchedEffect(streamData) {
        TyPlayer.playNewVideo(streamData)
      }

      LaunchedEffect(orientation) {
        if (orientation == ORIENTATION_LANDSCAPE) {
          dispatch(v(":player_fullscreen_landscape"))
        } else {
          dispatch(v(":player_portrait"))
        }
      }

      LaunchedEffect(Unit) {
        regPlaybackFxs(scope)
        regPlaybackEvents()
      }

      AndroidView(
        modifier = modifier
          .fillMaxWidth()
          .apply {
            if (orientation == ORIENTATION_LANDSCAPE) {
              padding(start = 26.dp)
            }
          },
        factory = {
          PlayerView(context).apply {
            setControllerVisibilityListener(controllerVisibilityListener)
            player = TyPlayer.getInstance()
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
          }
        }
      ) {
        // setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        it.useController = useController
      }

      if (showThumbnail == true) {
        Thumbnail(
          modifier = Modifier.wrapContentSize(),
          url = thumbnail
        )
      }

      if (
        playerState == PlayerState.LOADING ||
        playerState == PlayerState.BUFFERING
      ) {
        CircularProgressIndicator(
          modifier = Modifier
            .align(Alignment.Center)
            .size(64.dp),
          color = Grey300.copy(alpha = .4f)
        )
      }

      if (showQualityControl && streamData != null) {
        TextButton(
          modifier = Modifier.align(Alignment.TopEnd),
          colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
          onClick = {
            dispatch(v("playback_fsm", "generate_quality_list"))
            showQualitiesSheet = true
          }
        ) {
          Text(text = get<String>(streamData, Stream.current_quality)!!)
        }

        IconButton(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 40.dp, bottom = 6.dp),
          onClick = { dispatchSync(v(":toggle_orientation")) }
        ) {
          Image(
            imageVector = Icons.Default.Fullscreen,
            contentDescription = "",
            colorFilter = ColorFilter.tint(
              MaterialTheme.colorScheme.onBackground
            )
          )
        }
      }
    }

    if (showQualitiesSheet) {
      val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
      val containerColor = Color(0xFF212121)
      ModalBottomSheet(
        modifier = Modifier
//          .offset(y = (-24).dp)
          .padding(horizontal = 10.dp)
          .fillMaxWidth()
          .wrapContentHeight(),
        dragHandle = null,
        shape = RoundedCornerShape(10.dp),
        containerColor = containerColor,
        onDismissRequest = { showQualitiesSheet = false },
        sheetState = sheetState
      ) {
        val resolutions =
          get<List<Pair<String, Int>>>(streamData, Stream.quality_list)!!
        QualityList(
          resolutions = resolutions,
          containerColor = containerColor
        )
      }
    }

    if (isCollapsed) {
      MiniPlayerControls(
        isPlaying = playerState == PlayerState.PLAYING,
        onClosePlayer = {
          dispatchSync(v("playback_fsm", "close_player"))
        }
      ) {
        dispatchSync(v("playback_fsm", "toggle_play_pause"))
      }
    } else {
      // FIXME:
      /*val systemUiController = rememberSystemUiController()
      val inDarkTheme = isSystemInDarkTheme()
      LaunchedEffect(inDarkTheme) {
        systemUiController.setSystemBarsColor(
          color = if (inDarkTheme) Color.Black else Color.White
        )
      }*/
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
    val typography = MaterialTheme.typography
    Text(
      text = streamTitle,
      style = typography.titleMedium,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
    )
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
    val string = buildAnnotatedString {
      withStyle(
        style = typography.bodySmall.copy(color = color).toSpanStyle()
      ) {
        append("$views  $date")
      }
      append(" ")
      withStyle(
        style = typography.bodySmall.copy(
          fontWeight = FontWeight.Bold
        ).toSpanStyle()
      ) {
        append("...more")
      }
    }
    Text(text = string)
  }
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

      Spacer(modifier = Modifier.width(16.dp))

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
private fun viewTypeface(style: TextStyle): Typeface {
  val resolver: FontFamily.Resolver = LocalFontFamilyResolver.current
  return remember(resolver, style) {
    resolver.resolve(
      fontFamily = style.fontFamily,
      fontWeight = style.fontWeight ?: FontWeight.Normal,
      fontStyle = style.fontStyle ?: FontStyle.Normal,
      fontSynthesis = style.fontSynthesis ?: FontSynthesis.All
    )
  }.value as Typeface
}

@Composable
fun CommentsSection(
  highlightedComment: Spanned?,
  modifier: Modifier = Modifier,
  containerColor: Color,
  commentsCount: String,
  commentAvatar: String? = null,
  onClick: () -> Unit = { }
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = containerColor,
    shape = RoundedCornerShape(12.dp)
  ) {
    Box(
      modifier = Modifier
        .clickable(onClick = onClick)
        .padding(vertical = 8.dp, horizontal = 12.dp)
        .padding(bottom = 4.dp)
    ) {
      Column {
        val typography = MaterialTheme.typography
        val bodySmall = typography.bodySmall
        val onSurface = MaterialTheme.colorScheme.onSurface

        val sectionTitle = buildAnnotatedString {
          withStyle(style = typography.labelLarge.toSpanStyle()) {
            append("Comments")
          }
          append(" ")
          withStyle(
            style = bodySmall.copy(
              color = onSurface.copy(alpha = .6f)
            ).toSpanStyle()
          ) {
            append(commentsCount)
          }
        }

        Text(text = sectionTitle)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(.9f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AuthorAvatar(url = commentAvatar, size = 24.dp)
            Spacer(modifier = Modifier.width(8.dp))

            val typeface: Typeface = viewTypeface(style = bodySmall)

            AndroidView(
              factory = { context ->
                TextView(context).apply {
                  textSize = bodySmall.fontSize.value
                  maxLines = 2
                  ellipsize = TextUtils.TruncateAt.END
                  setTypeface(typeface)
                  setupClickableLinks()
                }
              }
            ) {
              it.setTextColor(onSurface.toArgb())
              it.text = highlightedComment
            }
          }
          Spacer(modifier = Modifier.padding(start = 12.dp))
          Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "",
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

private fun TextView.setupClickableLinks() {
  autoLinkMask = Linkify.WEB_URLS
  linksClickable = true
  // setting the color to use for highlighting the links
  setLinkTextColor(Blue300.toArgb())
}

@Composable
fun Html(text: String?) {
  AndroidView(
    factory = { context ->
      TextView(context).apply {
        setupClickableLinks()
      }
    },
    modifier = Modifier
      .padding(horizontal = 12.dp)
      .padding(top = 8.dp)
  ) {
    it.text = fromHtml(text ?: "", FROM_HTML_MODE_LEGACY)
  }
}

@Composable
fun SheetHeader(
  onTapHeader: () -> Unit,
  headerTitle: String,
  closeSheet: () -> Unit
) {
  val density = LocalDensity.current
  val px = with(density) {
    LocalConfiguration.current.screenWidthDp.dp.toPx()
  }
  Column {
    Row(
      modifier = Modifier
        .pointerInput(Unit) {
          detectTapGestures { offset ->
            val width = with(density) { 50.dp.toPx() }
            val fl = width / 2
            if (offset.x > (px / 2) - fl && offset.x < (px / 2) + fl) {
              onTapHeader()
            }
          }
        }
        .fillMaxWidth()
        .padding(start = 16.dp, end = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = headerTitle,
        style = MaterialTheme.typography.titleMedium
      )
      IconButton(onClick = closeSheet) {
        Icon(
          modifier = Modifier.size(32.dp),
          imageVector = Icons.Default.Close,
          contentDescription = ""
        )
      }
    }
    Divider(modifier = Modifier.fillMaxWidth())
  }
}

class BottomSheetNestedScrollConnection : NestedScrollConnection {
  private var lasPos: NestedScrollSource? = null
  var consume: Offset? = null
  override fun onPostScroll(
    consumed: Offset,
    available: Offset,
    source: NestedScrollSource
  ): Offset {
    if (source == NestedScrollSource.Fling) {
      lasPos = null
      consume = null
    }

    return if (consumed.x == 0f &&
      consumed.y == 0f &&
      lasPos == null
    ) {
      super.onPostScroll(consumed, available, source)
    } else {
      if (lasPos == null && source != NestedScrollSource.Fling) {
        lasPos = source
        consume = consumed
      }
      available
    }
  }
}

@Composable
@kotlin.OptIn(ExperimentalMaterial3Api::class)
fun PlaybackBottomSheet(
  isCollapsed: Boolean,
  onCollapsedClick: () -> Unit,
  streamData: IPersistentMap<Any, Any>?,
  playerRegion: PlayerState?,
  showThumbnail: Boolean?,
  thumbnail: String?,
  sheetPeekHeight: Dp
) {
  val descSheetState = rememberStandardBottomSheetState(
    initialValue = Hidden,
    skipHiddenState = false
  )
  val descScaffoldState = rememberBottomSheetScaffoldState(descSheetState)
  val playbackScope = rememberCoroutineScope()
  val decSheetValue = descSheetState.currentValue
  val descTargetValue = descSheetState.targetValue
  val descSheetPeekHeight = remember(decSheetValue, descTargetValue) {
    when {
      descTargetValue == Hidden && decSheetValue == Hidden -> 0.dp

      else -> sheetPeekHeight
    }
  }

  BottomSheetScaffold(
    scaffoldState = descScaffoldState,
    sheetPeekHeight = descSheetPeekHeight,
    sheetDragHandle = { DragHandle() },
    sheetContent = {
      HeadedSheetColumn(
        sheetState = descSheetState,
        sheetPeekHeight = sheetPeekHeight,
        header = {
          SheetHeader(
            onTapHeader = {
              playbackScope.launch {
                if (descSheetState.currentValue == PartiallyExpanded) {
                  descSheetState.expand()
                } else if (descSheetState.currentValue == Expanded) {
                  descSheetState.partialExpand()
                }
              }
            },
            headerTitle = "Description"
          ) {
            playbackScope.launch { descSheetState.hide() }
          }
        }
      ) {
        val streamDescription = get<String>(streamData, Stream.description)
        if (streamDescription != null) {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .nestedScroll(BottomSheetNestedScrollConnection())
          ) {
            item { Html(text = streamDescription) }
          }
        }
      }
    }
  ) {
    val commentsSheetState = rememberStandardBottomSheetState(
      initialValue = Hidden,
      skipHiddenState = false
    )
    val commentsScaffoldState = rememberBottomSheetScaffoldState(
      bottomSheetState = commentsSheetState
    )
    val commentsSheetValue = commentsSheetState.currentValue
    val commentsTargetValue = commentsSheetState.targetValue
    val commentsSheetPeekHeight =
      remember(commentsSheetValue, commentsTargetValue) {
        when {
          commentsTargetValue == Hidden && commentsSheetValue == Hidden -> 0.dp

          else -> sheetPeekHeight
        }
      }

    BottomSheetScaffold(
      scaffoldState = commentsScaffoldState,
      sheetPeekHeight = commentsSheetPeekHeight,
      sheetDragHandle = { DragHandle() },
      sheetContent = {
        HeadedSheetColumn(
          sheetState = commentsSheetState,
          sheetPeekHeight = sheetPeekHeight,
          header = {
            SheetHeader(
              onTapHeader = {
                playbackScope.launch {
                  if (commentsSheetState.currentValue == PartiallyExpanded) {
                    commentsSheetState.expand()
                  } else if (commentsSheetState.currentValue == Expanded) {
                    commentsSheetState.partialExpand()
                  }
                }
              },
              headerTitle = "Comments"
            ) {
              playbackScope.launch { commentsSheetState.hide() }
            }
          }
        ) {
          val appendingPanelVm = watch<AppendingPanelVm>(query = v("comments"))
          SwipeRefresh(
            state = rememberSwipeRefreshState(
              isRefreshing = appendingPanelVm.isRefreshing
            ),
            onRefresh = { dispatch(v("playback_fsm", "refresh_comments")) },
            indicator = { state, refreshTrigger ->
              val colorScheme = MaterialTheme.colorScheme
              SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = refreshTrigger,
                backgroundColor = colorScheme.primaryContainer,
                contentColor = colorScheme.onBackground,
                elevation = if (isSystemInDarkTheme()) 0.dp else 4.dp
              )
            }
          ) {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .nestedScroll(BottomSheetNestedScrollConnection())
            ) {
              val comments = appendingPanelVm.items
              itemsIndexed(items = comments.list) { index: Int, comment: Any ->
                dispatch(v("append_comments", index))

                val authorHandle: String = get(comment, "author")!!
                val authorAvatar: String = get(comment, "author_avatar")!!
                val commentText: Spanned = get(comment, "comment_text")!!
                val likesCount: String = get(comment, "likes_count")!!
                val repliesCount: Int = get(comment, "replies_count")!!

                val typography = MaterialTheme.typography
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable { }
                    .padding(12.dp)
                ) {
                  AuthorAvatar(url = authorAvatar, size = 24.dp)
                  Spacer(modifier = Modifier.width(16.dp))
                  Column {
                    val colorScheme = MaterialTheme.colorScheme
                    Text(
                      text = authorHandle,
                      style = typography.bodySmall.copy(
                        color = colorScheme.onSurface.copy(alpha = .6f)
                      )
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    /* val typeface: Typeface =
                       viewTypeface(style = typography.bodyMedium)

                     AndroidView(
                       factory = { context ->
                         TextView(context).apply {
                           textSize = typography.bodyMedium.fontSize.value
                           maxLines = 4
                           ellipsize = TextUtils.TruncateAt.END
                           setTypeface(typeface)
                           setupClickableLinks()
                         }
                       }
                     ) {
                       it.setTextColor(colorScheme.onSurface.toArgb())
                       it.text = commentText
                     }*/

                    // FIXME: Make links clickable
                    ExpandableText(
                      text = commentText.toString(),
                      modifier = Modifier,
                      minimizedMaxLines = 4,
                      style = typography.bodyMedium
                    )
                    /*     Text(
                           text = commentText,
                           maxLines = 4,
                           overflow = TextOverflow.Ellipsis,
                           style = typography.bodyMedium
                         )*/

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                      val size = 16.dp
                      Icon(
                        modifier = Modifier.size(size),
                        imageVector = Icons.Outlined.ThumbUp,
                        contentDescription = ""
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = likesCount,
                        style = typography.labelMedium
                      )

                      Spacer(modifier = Modifier.width(16.dp))
                      Icon(
                        modifier = Modifier.size(size),
                        imageVector = Icons.Outlined.ThumbDown,
                        contentDescription = ""
                      )
                    }
                  }
                }
                if (repliesCount > 0) {
                  Text(
                    modifier = Modifier
                      .padding(start = 40.dp)
                      .clickable { }
                      .padding(12.dp),
                    text = "$repliesCount replies",
                    style = typography.labelLarge.copy(color = Blue300)
                  )
                }
              }
              if (appendingPanelVm.isAppending) {
                item {
                  AppendingLoader()
                }
              }
            }
          }
        }
      }
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .clickable(isCollapsed, onClick = onCollapsedClick)
      ) {
        VideoPlayer(
          streamData = streamData,
          useController = !isCollapsed,
          isCollapsed = isCollapsed,
          playerState = playerRegion,
          showThumbnail = showThumbnail,
          thumbnail = thumbnail
        )

        if (streamData == null) return@Column

        DescriptionSection(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              playbackScope.launch { descSheetState.show() }
            }
            .padding(horizontal = 12.dp),
          streamTitle = get<String>(streamData, Stream.title)!!,
          views = get<String>(streamData, Stream.views)!!,
          date = get<String>(streamData, Stream.date)!!
        )

        ChannelSection(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 12.dp),
          channelName = get<String>(streamData, Stream.channel_name)!!,
          channelAvatar = get<String>(streamData, Stream.avatar)!!,
          subscribersCount = get<String>(streamData, Stream.sub_count)!!
        )

        Spacer(modifier = Modifier.height(8.dp))

        val colorScheme = MaterialTheme.colorScheme
        val alpha = if (isSystemInDarkTheme()) .09f else .05f
        val buttonsColor: Color = colorScheme.onBackground.copy(alpha)
        LikeSection(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
          likesCount = get<String>(streamData, Stream.likes_count)!!,
          buttonsColor = buttonsColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        val highlightedComment =
          get<Spanned>(streamData, Stream.highlight_comment)
        val commentAvatar =
          get<String>(streamData, Stream.highlight_comment_avatar)
        CommentsSection(
          highlightedComment = highlightedComment,
          modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
          containerColor = buttonsColor,
          commentsCount = get<String>(streamData, Stream.comments_count) ?: "",
          commentAvatar = commentAvatar
        ) {
          playbackScope.launch {
            commentsSheetState.partialExpand()
          }
        }
      }
    }
  }
}

val MINI_PLAYER_HEIGHT = 62.dp

// -- Previews -----------------------------------------------------------------

/*
@Preview(showBackground = true)
@Composable
fun StreamDescriptionPreview() {
  DescriptionSection(
    streamTitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
      "sed do eiusmod tempor incididunt ut labore et dolore magna ",
    views = "4.6M",
    date = "3y ago"
  )
}

@Preview(showBackground = true)
@Composable
fun StreamChannel1Preview() {
  ChannelSection(
    channelName = "Lorem",
    channelAvatar = "Channel Name",
    subscribersCount = "48M"
  )
}

@Preview(showBackground = true)
@Composable
fun StreamChannel2Preview() {
  ChannelSection(
    channelName = "Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet,",
    channelAvatar = "Channel Name",
    subscribersCount = "48M"
  )
}

@Preview(showBackground = true)
@Composable
fun LikeSectionPreview() {
  val colorScheme = MaterialTheme.colorScheme
  val buttonsColor: Color = colorScheme.onBackground.copy(.05f)
  LikeSection(likesCount = "25K", buttonsColor = buttonsColor)
}

@Preview(showBackground = true)
@Composable
fun CommentsSectionPreview() {
  TyTheme(darkTheme = true) {
    val colorScheme = MaterialTheme.colorScheme
    val buttonsColor: Color = colorScheme.onBackground.copy(.2f)

    CommentsSection(
      highlightedComment = fromHtml(
        "Lorem ipsum dolor sit amet Lorem ipsum dolor sit" +
          " amet, sit amet Lorem \uD83D\uDC90 ipsum dolor sit amet Lorem ipsum" +
          " dolor sit amet" +
          " Lorem ipsum dolor ",
        FROM_HTML_MODE_LEGACY
      ),
      containerColor = buttonsColor,
      commentsCount = "21K"
    )
  }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CommentsListPreview() {
  CommentsList(
    comments = Comments(
      v(
        m(
          "author" to "@john_doe $VIDEO_INFO_DIVIDER 3w ago",
          "author_avatar" to "url",
          "comment_text" to "Lorem ipsum dolor sit amet Lorem ipsum dolor",
          "likes_count" to "1.9k",
          "replies_count" to 0
        )
      )
    )
  )
}
*/

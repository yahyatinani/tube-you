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
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.ripple.LocalRippleTheme
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
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.SheetValue.PartiallyExpanded
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberPlainTooltipState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.R.string
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AppendingLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AuthorAvatar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AvatarLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.component.CountText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ExpandableText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.IconBorder
import com.github.yahyatinani.tubeyou.modules.designsystem.component.StreamLoaderPortrait
import com.github.yahyatinani.tubeyou.modules.designsystem.component.SubscribeButton
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TextLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.component.Thumbnail
import com.github.yahyatinani.tubeyou.modules.designsystem.component.TyIconRoundedButton
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue300
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue400
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Grey300
import com.github.yahyatinani.tubeyou.modules.panel.common.R
import com.github.yahyatinani.tubeyou.modules.panel.common.Stream
import com.github.yahyatinani.tubeyou.modules.panel.common.UIState
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm.COMMENTS_ROUTE
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm.CommentsListState
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm.REPLIES_ROUTE
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm.StreamState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.recompose.regFx
import io.github.yahyatinani.recompose.watch
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
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
fun DragHandle(modifier: Modifier = Modifier, onClick: () -> Unit = { }) {
  Surface(
    modifier = modifier
      .wrapContentSize()
      .padding(top = 8.dp, bottom = 2.dp)
      .semantics { contentDescription = "dragHandleDescription" },
    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .2f),
    shape = MaterialTheme.shapes.extraLarge
  ) {
    Box(
      Modifier
        .clickable(onClick = onClick)
        .size(width = 40.0.dp, height = 4.0.dp)
    )
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
  modifier: Modifier = Modifier,
  isPlaying: Boolean,
  onClosePlayer: () -> Unit = { },
  playPausePlayer: () -> Unit = { }
) {
  with(LocalContext.current.findWindow()) {
    this?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
  }
  Row(
    modifier = modifier.height(110.dp - 48.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val size = 32.dp
    val colorFilter = ColorFilter.tint(
      color = MaterialTheme.colorScheme.onBackground
    )
    IconButton(onClick = playPausePlayer) {
      Image(
        imageVector = when (isPlaying) {
          true -> Icons.Default.Pause
          else -> Icons.Default.PlayArrow
        },
        modifier = Modifier.size(size),
        contentDescription = "play/pause",
        colorFilter = colorFilter
      )
    }

    IconButton(onClick = onClosePlayer) {
      Image(
        imageVector = Icons.Default.Close,
        modifier = Modifier.size(size),
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
  stream: UIState,
  useController: Boolean = true,
  showThumbnail: Boolean?,
  thumbnail: String?
) {
  val streamData = stream.data as IPersistentMap<Any, Any>

  val playerState = get<StreamState>(stream.data, common.state)
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val orientation = LocalConfiguration.current.orientation

  var showQualityControl: Boolean by remember { mutableStateOf(false) }
  var showQualitiesSheet: Boolean by remember { mutableStateOf(false) }

  val controllerVisibilityListener = remember {
    PlayerView.ControllerVisibilityListener { visibility: Int ->
      showQualityControl = visibility == View.VISIBLE
    }
  }

  Box(modifier = modifier) {
    LaunchedEffect(streamData) {
      if (streamData[common.state] != StreamState.LOADING) {
        TyPlayer.playNewVideo(streamData)
      }
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
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.Center),
      factory = {
        PlayerView(context).apply {
          setControllerVisibilityListener(controllerVisibilityListener)
          player = TyPlayer.getInstance()
          resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
          if (orientation == ORIENTATION_LANDSCAPE) {
            setBackgroundColor(ContextCompat.getColor(context, R.color.black))
          }
        }
      }
    ) {
      if (it.videoSurfaceView != null) {
        it.videoSurfaceView!!.layoutParams.height =
          ViewGroup.LayoutParams.MATCH_PARENT
      }

      it.useController = useController
    }

    if (showThumbnail == true) {
      Thumbnail(
        modifier = Modifier.wrapContentSize(),
        url = thumbnail
      )
    }

    if (
      playerState == StreamState.LOADING ||
      playerState == StreamState.BUFFERING
    ) {
      CircularProgressIndicator(
        modifier = Modifier
          .align(Alignment.Center)
          .size(64.dp),
        color = Grey300.copy(alpha = .4f)
      )
    }

    if (showQualityControl && playerState != StreamState.LOADING) {
      TextButton(
        modifier = Modifier.align(Alignment.TopEnd),
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
        onClick = {
          dispatch(v("stream_panel_fsm", "generate_quality_list"))
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
    StreamPanelTitle(streamTitle)

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
      text = stringResource(string.subscribe),
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
  commentsDisabled: Boolean,
  onClick: () -> Unit = { }
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = containerColor,
    shape = RoundedCornerShape(12.dp)
  ) {
    Box(
      modifier = Modifier
        .clickable(onClick = onClick, enabled = !commentsDisabled)
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
          if (commentsDisabled) {
            Text(
              text = stringResource(R.string.comments_are_turned_off),
              style = bodySmall
            )
          } else {
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
  setLinkTextColor(Blue400.toArgb())
}

@Composable
fun Html(text: Spanned) {
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
    it.text = text
  }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetHeader(
  modifier: Modifier = Modifier,
  headerTitle: String = "",
  header: (@Composable () -> Unit)? = null,
  sheetState: SheetValue,
  closeSheet: () -> Unit
) {
  Column {
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(end = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        when {
          header != null -> header()
          else -> {
            Text(
              text = headerTitle,
              style = MaterialTheme.typography.titleMedium
            )
          }
        }
      }
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

  BackHandler(enabled = sheetState != Hidden) {
    closeSheet()
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

  override suspend fun onPostFling(
    consumed: Velocity,
    available: Velocity
  ): Velocity = available
}

object BlockScrolling : NestedScrollConnection {
  override fun onPostScroll(
    consumed: Offset,
    available: Offset,
    source: NestedScrollSource
  ): Offset = available
}

@Composable
private fun HeartedAvatar(content: @Composable () -> Unit) {
  Layout(
    content = content
  ) { measurables, constraints ->
    require(measurables.size == 2)
    val placeables: List<Placeable> =
      measurables.map { measurable: Measurable ->
        measurable.measure(
          constraints.copy(
            minWidth = 0,
            minHeight = 0
          )
        )
      }
    val avatar = placeables.first()
    val heart = placeables.last()

    val width = constraints.maxWidth
    val height = constraints.maxHeight
    layout(
      width = width,
      height = height
    ) {
      val centerX = width / 2
      val centerY = height / 2
      avatar.placeRelative(
        x = centerX - (avatar.width / 2),
        y = centerY - (avatar.height / 2)
      )
      heart.placeRelative(
        x = centerX,
        y = centerY
      )
    }
  }
}

@Composable
fun StreamPanelTitle(streamTitle: String, maxLines: Int = 2) {
  Text(
    text = streamTitle,
    style = MaterialTheme.typography.titleMedium,
    maxLines = maxLines,
    overflow = TextOverflow.Ellipsis
  )
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DescriptionSheet(
  descSheetState: SheetState,
  sheetPeekHeight: Dp,
  uiState: UIState
) {
  Scaffold(
    modifier = Modifier
      .then(
        if (descSheetState.currentValue == PartiallyExpanded) {
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
        sheetState = descSheetState.currentValue
      ) {
        dispatch(v("stream_panel_fsm", "close_desc_sheet"))
      }
    }
  ) { padding ->
    val sheetData = uiState.data
    val typography = MaterialTheme.typography
    val bodySmall = typography.bodySmall
    val colorScheme = MaterialTheme.colorScheme

    if (get<StreamState>(sheetData, common.state)!! == StreamState.LOADING) {
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
          StreamPanelTitle(
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
            StreamPanelTitle(get<String>(sheetData, Stream.likes_count)!!)
            Text(
              text = "Likes",
              style = bodySmall.copy(colorScheme.onSurface.copy(alpha = .6f))
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StreamPanelTitle(get<String>(sheetData, Stream.views_full)!!)
            Text(
              text = "Views",
              style = bodySmall.copy(colorScheme.onSurface.copy(alpha = .6f))
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StreamPanelTitle(get<String>(sheetData, Stream.month_day)!!)
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

  BackHandler(enabled = descSheetState.currentValue != Hidden) {
    dispatch(v("stream_panel_fsm", "close_desc_sheet"))
  }
}

@Composable
@kotlin.OptIn(ExperimentalMaterial3Api::class)
fun Comment(
  modifier: Modifier = Modifier,
  backgroundColor: Color = MaterialTheme.colorScheme.background,
  state: UIState,
  isCommentExpanded: Boolean = false,
  onClick: () -> Unit = {}
) {
  val comment = state.data
  val author: String = get(comment, "author")!!
  val commentedTime: String = get(comment, "commentedTime")!!
  val authorAvatar: String = get(comment, "author_avatar")!!
  val commentText: AnnotatedString =
    get(comment, "comment_text")!!
  val likesCount: String = get(comment, "likes_count")!!
  val verified: Boolean = get(comment, "verified")!!
  val pinned: Boolean = get(comment, "pinned")!!
  val hearted: Boolean = get(comment, "hearted")!!
  val byUploader: Boolean = get(comment, "by_uploader")!!

  val typography = MaterialTheme.typography
  Row(
    modifier = Modifier
      .background(backgroundColor)
      .fillMaxWidth()
      .wrapContentHeight()
      .clickable(onClick = onClick)
      .padding(12.dp)
      .then(modifier)
  ) {
    AuthorAvatar(url = authorAvatar, size = 24.dp)

    Spacer(modifier = Modifier.width(12.dp))

    Column(
      modifier = Modifier.padding(end = 24.dp)
    ) {
      val colorScheme = MaterialTheme.colorScheme
      val color = colorScheme.onSurface.copy(alpha = .6f)
      val textStyle = typography.bodySmall.copy(color = color)

      if (pinned) {
        val uploader: String = get(comment, "uploader")!!
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            modifier = Modifier.size(12.dp),
            imageVector = Icons.Default.PushPin,
            contentDescription = "",
            tint = color
          )

          Spacer(modifier = Modifier.width(4.dp))

          Text(
            text = stringResource(R.string.pinned_by),
            style = textStyle
          )

          Text(
            text = " $uploader",
            style = textStyle
          )
        }

        Spacer(modifier = Modifier.height(6.dp))
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Row(
          modifier = Modifier
            .then(
              if (byUploader) {
                Modifier
                  .background(
                    color = colorScheme.onSurface.copy(.6f),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .padding(horizontal = 6.dp)
              } else {
                Modifier
              }
            ),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = author,
            style = when {
              byUploader -> textStyle.copy(color = Color.White)
              else -> textStyle
            }
          )

          if (verified) {
            Icon(
              modifier = Modifier.size(14.dp),
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "",
              tint = Color.White
            )
          }
        }

        Text(
          text = commentedTime,
          style = textStyle
        )
      }

      Spacer(modifier = Modifier.height(2.dp))

      ExpandableText(
        text = commentText,
        modifier = Modifier,
        minimizedMaxLines = 4,
        style = typography.bodyMedium.copy(
          color = colorScheme.onSurface
        ),
        isExpanded = isCommentExpanded
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
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

        if (hearted) {
          Spacer(modifier = Modifier.width(20.dp))

          val tooltipState = rememberPlainTooltipState()
          val uploader: String = get(comment, "uploader")!!

          PlainTooltipBox(
            tooltip = {
              Text(
                text = "❤\uFE0F by $uploader",
                modifier = Modifier.padding(10.dp),
                style = typography.bodyMedium
              )
            },
            tooltipState = tooltipState,
            containerColor = colorScheme.onSurface
          ) {
            val scope = rememberCoroutineScope()
            IconButton(
              onClick = { scope.launch { tooltipState.show() } }
            ) {
              HeartedAvatar {
                AuthorAvatar(
                  url = get<String>(comment, "uploader_avatar")!!,
                  size = 16.dp
                )
                IconBorder(
                  imageVector = Icons.Default.Favorite,
                  colorScheme = colorScheme,
                  tint = Color.Red
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CommentReplies(modifier: Modifier = Modifier, repliesState: UIState) {
  val commentsState = get<CommentsListState>(repliesState.data, common.state)

  Surface {
    val theme = LocalRippleTheme.current
    val highlightColor = theme
      .defaultColor()
      .copy(alpha = theme.rippleAlpha().pressedAlpha)
    Box {
      LazyColumn(
        modifier = modifier
          .fillMaxSize()
          .nestedScroll(BottomSheetNestedScrollConnection())
      ) {
        if (commentsState == CommentsListState.LOADING) {
          return@LazyColumn
        }

        item {
          Comment(
            backgroundColor = highlightColor,
            state = get<UIState>(repliesState.data, "selected_comment")!!,
            isCommentExpanded = true
          )
        }

        val repliesList =
          get<List<UIState>>(repliesState.data, "replies_list")!!

        itemsIndexed(items = repliesList) { index, comment: UIState ->
          dispatch(v("append_replies", index))
          Comment(
            modifier = Modifier.padding(start = 36.dp),
            state = comment
          )
        }

        if (commentsState == CommentsListState.APPENDING) {
          item { AppendingLoader() }
        }
      }

      if (commentsState == CommentsListState.LOADING) {
        CircularProgressIndicator(
          modifier = Modifier.align(Alignment.Center),
          color = Blue300
        )
      }

      /* if (panelVm.error != null) {
         // TODO: Implement proper UI for errors. Also, make it an argument.
         Text(text = "Request failed! Error: ${panelVm.error}")
       }*/
    }
  }

  BackHandler {
    dispatchSync(v("nav_back_to_comments"))
  }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentsSheet(
  commentsSheetState: SheetState,
  sheetPeekHeight: Dp,
  uiState: UIState
) {
  val data = uiState.data
  val route = get<String>(data, "current_route")!!
  Scaffold(
    modifier = Modifier
      .then(
        if (commentsSheetState.currentValue == PartiallyExpanded) {
          Modifier.height(sheetPeekHeight)
        } else {
          Modifier
        }
      ),
    topBar = {
      val isComments = route == COMMENTS_ROUTE
      SheetHeader(
        header = {
          Box {
            AnimatedVisibility(
              modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterStart),
              visible = isComments,
              enter = fadeIn(),
              exit = fadeOut()
            ) {
              Text(
                text = stringResource(R.string.comments_bottom_sheet_title),
                style = MaterialTheme.typography.titleMedium
              )
            }

            val enter =
              slideInHorizontally(initialOffsetX = { it / 4 }) + fadeIn()
            val exit =
              slideOutHorizontally(targetOffsetX = { it / 8 }) + fadeOut()
            AnimatedVisibility(
              visible = !isComments,
              enter = enter,
              exit = exit
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { dispatchSync(v("nav_back_to_comments")) }
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = ""
                  )
                }
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                  text = stringResource(R.string.replies_bottom_sheet_title),
                  style = MaterialTheme.typography.titleMedium
                )
              }
            }
          }
        },
        sheetState = commentsSheetState.currentValue
      ) {
        dispatch(v("stream_panel_fsm", "close_comments_sheet"))
      }
    }
  ) { padding: PaddingValues ->
    val context = LocalContext.current
    val commentsNavController = rememberNavController()
    rememberSaveable(
      saver = Saver(
        save = { it.saveState() },
        restore = {
          NavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            navigatorProvider.addNavigator(DialogNavigator())
          }.apply { restoreState(it) }
        }
      )
    ) {
      NavHostController(context).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
        navigatorProvider.addNavigator(DialogNavigator())
      }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
      regFx("nav_comment_replies") {
        scope.launch {
          commentsNavController.navigate(REPLIES_ROUTE)
        }
      }

      regFx("nav_back_to_comments") {
        scope.launch {
          commentsNavController.popBackStack(COMMENTS_ROUTE, inclusive = false)
          dispatch(v("stream_panel_fsm", "nav_back_to_comments"))
        }
      }

      regEventFx("nav_back_to_comments") { _, _ ->
        m(fx to v(v("nav_back_to_comments")))
      }
    }

    NavHost(
      modifier = Modifier
        .padding(padding)
        .fillMaxWidth(),
      navController = commentsNavController,
      startDestination = COMMENTS_ROUTE
    ) {
      commentsList(get<UIState>(data, "comments")!!)

      repliesList(get<UIState>(data, "replies")!!)
    }
  }
}

private fun NavGraphBuilder.repliesList(repliesState: UIState) {
  composable(
    route = REPLIES_ROUTE,
    enterTransition = {
      slideInHorizontally(
        tween(500),
        initialOffsetX = { it }
      )
    },
    exitTransition = {
      slideOutHorizontally(
        tween(500),
        targetOffsetX = { it }
      )
    }
  ) {
    val listState = get<CommentsListState>(repliesState.data, common.state)
    SwipeRefresh(
      modifier = Modifier
        .fillMaxSize()
        .nestedScroll(BlockScrolling),
      state = rememberSwipeRefreshState(
        isRefreshing = listState == CommentsListState.REFRESHING
      ),
      onRefresh = {
        dispatch(v("stream_panel_fsm", "refresh_comment_replies"))
      },
      indicator = { swipeRefreshState, refreshTrigger ->
        val colorScheme = MaterialTheme.colorScheme
        SwipeRefreshIndicator(
          state = swipeRefreshState,
          refreshTriggerDistance = refreshTrigger,
          backgroundColor = colorScheme.primaryContainer,
          contentColor = colorScheme.onBackground,
          elevation = if (isSystemInDarkTheme()) 0.dp else 4.dp
        )
      }
    ) {
      CommentReplies(repliesState = repliesState)
    }
  }
}

private fun NavGraphBuilder.commentsList(uiState: UIState) {
  composable(COMMENTS_ROUTE) {
    val uiData = uiState.data
    val state = get<CommentsListState>(uiData, common.state)
    SwipeRefresh(
      modifier = Modifier
        .fillMaxSize()
        .nestedScroll(BlockScrolling),
      state = rememberSwipeRefreshState(
        isRefreshing = state == CommentsListState.REFRESHING
      ),
      onRefresh = { dispatch(v("stream_panel_fsm", "refresh_comments")) },
      indicator = { refreshState, refreshTrigger ->
        val colorScheme = MaterialTheme.colorScheme
        SwipeRefreshIndicator(
          state = refreshState,
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
        if (state == CommentsListState.LOADING) return@LazyColumn

        val commentsList = get<List<UIState>>(uiData, "comments_list")!!
        itemsIndexed(items = commentsList) { index: Int, comment: UIState ->
          dispatch(v("append_comments", index))
          val typography = MaterialTheme.typography

          val indexComment = index to comment
          Comment(state = comment) {
            dispatch(v("stream_panel_fsm", "navigate_replies", indexComment))
          }

          val repliesCount: Int = get(comment.data, "replies_count")!!
          if (repliesCount > 0) {
            Text(
              modifier = Modifier
                .padding(start = 40.dp)
                .clickable(onClick = {
                  dispatch(
                    v(
                      "stream_panel_fsm",
                      "navigate_replies",
                      indexComment
                    )
                  )
                })
                .padding(12.dp),
              text = "$repliesCount replies",
              style = typography.labelLarge.copy(color = Blue400)
            )
          }
        }

        item {
          if (state == CommentsListState.APPENDING) {
            AppendingLoader()
          }
        }
      }
    }
  }
}

@Composable
@kotlin.OptIn(ExperimentalMaterial3Api::class)
fun PlaybackBottomSheet(
  isCollapsed: Boolean,
  onCollapsedClick: () -> Unit,
  activeStream: UIState,
  activeStreamCache: VideoViewModel,
  showThumbnail: Boolean?,
  sheetPeekHeight: Dp,
  onClosePlayer: () -> Unit = { }
) {
  val descSheetState = rememberStandardBottomSheetState(
    initialValue = Hidden,
    skipHiddenState = false
  )
  val descScaffoldState = rememberBottomSheetScaffoldState(descSheetState)
  val playbackScope = rememberCoroutineScope()
  val descriptionSheetState = watch<SheetValue>(query = v("description_sheet"))
  val descSheetValue = descSheetState.currentValue
  val descTargetValue = descSheetState.targetValue
  val descSheetPeekHeight =
    remember(descriptionSheetState, descTargetValue, descSheetValue) {
      if (descriptionSheetState == Hidden) {
        0.dp
      } else if (descSheetValue == Hidden && descTargetValue == Hidden) {
        dispatch(v("stream_panel_fsm", "close_desc_sheet"))
        0.dp
      } else {
        sheetPeekHeight
      }
    }

  LaunchedEffect(Unit) {
    regFx(id = "half_expand_desc_sheet") {
      playbackScope.launch { descSheetState.partialExpand() }
    }

    regFx(id = "close_desc_sheet") {
      playbackScope.launch { descSheetState.hide() }
    }
  }

  val streamData = activeStream.data
  val streamState = remember(activeStream) {
    get<StreamState>(streamData, common.state)!!
  }
  val isLoading = remember(streamState) { streamState == StreamState.LOADING }
  val height = remember(sheetPeekHeight) { sheetPeekHeight - 18.dp }
  BottomSheetScaffold(
    scaffoldState = descScaffoldState,
    sheetPeekHeight = descSheetPeekHeight,
    sheetDragHandle = {
      DragHandle {
        playbackScope.launch {
          if (descSheetValue == PartiallyExpanded) {
            descSheetState.expand()
          } else if (descSheetValue == Expanded) {
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
      initialValue = Hidden,
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
          commentsSheetValue == Hidden -> 0.dp
          commentsTargetValue == Hidden &&
            commentsCurrentValue == Hidden -> {
            dispatch(v("stream_panel_fsm", "close_comments_sheet"))
            0.dp
          }

          else -> sheetPeekHeight
        }
      }

    LaunchedEffect(Unit) {
      regFx(id = "half_expand_comments_sheet") {
        playbackScope.launch { commentsSheetState.partialExpand() }
      }

      regFx(id = "close_comments_sheet") {
        playbackScope.launch {
          commentsSheetState.hide()
          dispatch(v("nav_back_to_comments"))
        }
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
            } else if (commentsSheetState.currentValue == Expanded) {
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
              stream = activeStream,
              useController = !isCollapsed,
              showThumbnail = showThumbnail,
              thumbnail = activeStreamCache.thumbnail
            )
            if (isCollapsed) {
              if (streamState != StreamState.LOADING) {
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
              onClosePlayer = onClosePlayer
            ) {
              dispatchSync(v("stream_panel_fsm", "toggle_play_pause"))
            }
          }
        }
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .nestedScroll(BlockScrolling)
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
                    StreamPanelTitle(streamTitle = activeStreamCache.title)
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
            if (get<CommentsListState>(
                commentsStateData,
                common.state
              ) == CommentsListState.LOADING
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
              commentsDisabled = commentsDisabled
            ) {
              dispatch(v("stream_panel_fsm", "half_expand_comments_sheet"))
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }

          repeat(2) {
            item {
              StreamLoaderPortrait(containerColor)
            }
          }
        }
      }
    }
  }
}

val MINI_PLAYER_HEIGHT = 56.dp

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

package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen

import android.graphics.Typeface
import android.text.Spanned
import android.text.TextUtils
import android.text.util.Linkify
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberPlainTooltipState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AppendingLoader
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AuthorAvatar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ExpandableText
import com.github.yahyatinani.tubeyou.modules.designsystem.component.IconBorder
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue300
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue400
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.yahyatinani.recompose.RegFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.modules.feature.watch.R
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.COMMENTS_ROUTE
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.ListState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.REPLIES_ROUTE
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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

private fun TextView.setupClickableLinks() {
  autoLinkMask = Linkify.WEB_URLS
  linksClickable = true
  // setting the color to use for highlighting the links
  setLinkTextColor(Blue400.toArgb())
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
            imageVector = TyIcons.ExpandArrow,
            contentDescription = "",
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Comment(
  modifier: Modifier = Modifier,
  backgroundColor: Color = MaterialTheme.colorScheme.background,
  state: UIState,
  isCommentExpanded: Boolean = false,
  commentTextStyle: TextStyle,
  onClick: () -> Unit = {}
) {
  val comment = state.data
  val author: String = get(comment, "author")!!
  val commentedTime: String = get(comment, "commentedTime")!!
  val authorAvatar: String = get(comment, "author_avatar")!!
  val commentText: AnnotatedString = get(comment, "comment_text")!!
  val likesCount: String = get(comment, "likes_count")!!
  val verified: Boolean = get(comment, "verified")!!
  val pinned: Boolean = get(comment, "pinned")!!
  val hearted: Boolean = get(comment, "hearted")!!
  val byUploader: Boolean = get(comment, "by_uploader")!!

  Row(
    modifier = Modifier
      .background(backgroundColor)
      .fillMaxWidth()
      .wrapContentHeight()
      .clickable(onClick = onClick)
      .padding(12.dp)
      .then(modifier.testTag("watch:comment"))
  ) {
    AuthorAvatar(url = authorAvatar, size = 24.dp)

    Spacer(modifier = Modifier.width(12.dp))

    Column(
      modifier = Modifier.padding(end = 24.dp)
    ) {
      val colorScheme = MaterialTheme.colorScheme
      val onSurface = colorScheme.onSurface
      val typography = MaterialTheme.typography
      val tint = remember(onSurface) { onSurface.copy(alpha = .6f) }
      val textStyle = remember(onSurface) {
        typography.bodySmall.copy(color = tint)
      }

      if (pinned) {
        val uploader: String = get(comment, "uploader")!!
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            modifier = Modifier.size(16.dp),
            imageVector = TyIcons.PushPin,
            contentDescription = "",
            tint = tint
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
            .let {
              if (byUploader) {
                it
                  .background(
                    color = tint,
                    shape = remember { RoundedCornerShape(12.dp) }
                  )
                  .padding(horizontal = 6.dp)
              } else {
                it
              }
            },
          verticalAlignment = Alignment.CenterVertically
        ) {
          val style = remember { textStyle.copy(color = Color.White) }
          Text(
            text = author,
            style = remember(byUploader) {
              if (byUploader) style else textStyle
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
        style = commentTextStyle,
        isExpanded = isCommentExpanded
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        val size = 16.dp
        Icon(
          modifier = Modifier.size(size),
          imageVector = TyIcons.ThumbUpOutlined400,
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
          imageVector = TyIcons.ThumbDownOutlined400,
          contentDescription = ""
        )

        if (hearted) {
          Spacer(modifier = Modifier.width(20.dp))

          val tooltipState = rememberPlainTooltipState() // FIXME:
          val uploader: String = get(comment, "uploader")!!

          PlainTooltipBox(
            tooltip = {
              Text(
                text = "❤\uFE0F by $uploader",
                modifier = Modifier.padding(10.dp),
                style = typography.bodyMedium
              )
            },
            tooltipState = rememberPlainTooltipState(),
            containerColor = onSurface
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
fun CommentReplies(
  modifier: Modifier = Modifier,
  repliesState: UIState,
  commentTextStyle: TextStyle
) {
  val commentsState = get<ListState>(repliesState.data, common.state)

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
        if (commentsState == ListState.LOADING) {
          return@LazyColumn
        }

        item {
          Comment(
            backgroundColor = highlightColor,
            state = get<UIState>(repliesState.data, "selected_comment")!!,
            isCommentExpanded = true,
            commentTextStyle = commentTextStyle
          )
        }

        val repliesList =
          get<List<UIState>>(repliesState.data, "replies_list")!!

        itemsIndexed(items = repliesList) { index, comment: UIState ->
          dispatch(v("append_replies", index))
          Comment(
            modifier = Modifier.padding(start = 36.dp),
            state = comment,
            commentTextStyle = commentTextStyle
          )
        }

        if (commentsState == ListState.APPENDING) {
          item { AppendingLoader() }
        }
      }

      if (commentsState == ListState.LOADING) {
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
    dispatch(v("nav_back_to_comments"))
  }
}

private fun NavGraphBuilder.commentsList(
  uiState: UIState,
  commentTextStyle: TextStyle
) {
  composable(COMMENTS_ROUTE) {
    val uiData = uiState.data
    SwipeRefresh(
      modifier = Modifier
        .testTag("watch:comments_list")
        .fillMaxSize()
        .nestedScroll(BlockScrolling),
      state = rememberSwipeRefreshState(
        isRefreshing = get(uiData, "is_refreshing", false)!!
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
      val textStyle = MaterialTheme.typography.labelLarge.copy(color = Blue400)

      if (get(uiData, "is_loading", false)!!) {
        Box(modifier = Modifier.fillMaxSize()) {
          CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = Blue300
          )
        }
        return@SwipeRefresh
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .nestedScroll(BottomSheetNestedScrollConnection())
      ) {
        itemsIndexed(
          items = get<List<UIState>>(uiData, "comments_list")!!
        ) { index: Int, comment: UIState ->
          dispatch(v("append_comments", index))
          val indexComment = index to comment
          Comment(
            state = comment,
            commentTextStyle = commentTextStyle,
            onClick = {
              dispatch(v("stream_panel_fsm", "navigate_replies", indexComment))
            }
          )

          val repliesCount: Int = get(comment.data, "replies_count")!!
          if (repliesCount > 0) {
            Text(
              modifier = Modifier
                .padding(start = 40.dp)
                .clickable(
                  onClick = {
                    dispatch(
                      v("stream_panel_fsm", "navigate_replies", indexComment)
                    )
                  }
                )
                .padding(12.dp)
                .testTag("watch:replies_count"),
              text = "$repliesCount replies",
              style = textStyle
            )
          }
        }

        item {
          if (get(uiData, "is_appending", false)!!) {
            AppendingLoader()
          }
        }
      }
    }
  }
}

private fun NavGraphBuilder.repliesList(
  repliesState: UIState?,
  commentTextStyle: TextStyle
) {
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
    if (repliesState == null) return@composable

    val listState = get<ListState>(repliesState.data, common.state)
    SwipeRefresh(
      modifier = Modifier
        .testTag("watch:replies_list")
        .fillMaxSize()
        .nestedScroll(BlockScrolling),
      state = rememberSwipeRefreshState(
        isRefreshing = listState == ListState.REFRESHING
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
      CommentReplies(
        repliesState = repliesState,
        commentTextStyle = commentTextStyle
      )
    }
  }
}

@Composable
private fun Header(
  isCommentsRoute: Boolean,
  onClick: () -> Unit
) {
  Box {
    AnimatedVisibility(
      modifier = Modifier
        .padding(start = 16.dp)
        .align(Alignment.CenterStart),
      visible = isCommentsRoute,
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
      visible = !isCommentsRoute,
      enter = enter,
      exit = exit
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onClick
        ) {
          Icon(
            imageVector = TyIcons.ArrowBack,
            modifier = Modifier.width(28.dp),
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
}

@Composable
internal fun CommentsSheet(
  uiState: UIState,
  contentHeight: Dp,
  isSheetOpen: Boolean,
  toggleExpansion: () -> Unit
) {
  val repliesState = get<UIState>(uiState.data, "replies")

  Scaffold(
    modifier = Modifier.heightIn(max = contentHeight),
    topBar = {
      SheetHeader(
        modifier = Modifier.testTag("watch:comments_top_bar"),
        header = {
          Header(
            isCommentsRoute = repliesState == null,
            onClick = { dispatch(v("nav_back_to_comments")) }
          )
        },
        isSheetOpen = isSheetOpen,
        closeSheet = {
          dispatch(v("stream_panel_fsm", "close_comments_sheet"))
        },
        toggleExpansion = toggleExpansion
      )
    }
  ) { padding: PaddingValues ->
    LaunchedEffect(Unit) {
      regEventFx(id = "nav_back_to_comments") { _, _ ->
        m(BuiltInFx.fx to v(v("nav_back_to_comments")))
      }
    }
    val scope = rememberCoroutineScope()
    val commentsNavController = rememberNavController()
    RegFx(id = "nav_comment_replies", commentsNavController) {
      scope.launch {
        commentsNavController.navigate(REPLIES_ROUTE)
      }
    }

    RegFx(id = "nav_back_to_comments", commentsNavController) {
      scope.launch {
        commentsNavController.popBackStack(COMMENTS_ROUTE, inclusive = false)
        dispatch(v("stream_panel_fsm", "nav_back_to_comments"))
      }
    }

    val commentTextStyle = MaterialTheme.typography.bodyMedium.copy(
      color = MaterialTheme.colorScheme.onSurface
    )
    NavHost(
      modifier = Modifier
        .padding(padding)
        .fillMaxWidth(),
      navController = commentsNavController,
      startDestination = COMMENTS_ROUTE
    ) {
      commentsList(
        uiState = uiState,
        commentTextStyle = commentTextStyle
      )

      repliesList(
        repliesState = repliesState,
        commentTextStyle = commentTextStyle
      )
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CommentsBottomSheetScaffold(
  commentsListState: UIState?,
  sheetUiState: UIState,
  onClickSheetHeader: () -> Unit,
  content: @Composable (PaddingValues) -> Unit
) {
  val commentsSheetState = rememberStandardBottomSheetState(
    initialValue = SheetValue.Hidden,
    skipHiddenState = false
  )
  val commentsScaffoldState = rememberBottomSheetScaffoldState(
    bottomSheetState = commentsSheetState
  )
  LaunchedEffect(Unit) {
    snapshotFlow { commentsSheetState.currentValue }
      .map { it == SheetValue.Hidden }
      .distinctUntilChanged()
      .filter { it }
      .collect {
        dispatch(v("stream_panel_fsm", "close_comments_sheet"))
      }
    /*    snapshotFlow { commentsSheetState.currentValue }
          .distinctUntilChanged()
          .collect {
            dispatch(v("stream_panel_fsm", v("comments_sheet", it)))
          }*/
  }
  val coroutineScope = rememberCoroutineScope()
  val sheetData = sheetUiState.data

  BottomSheetScaffold(
    sheetContent = {
      RegFx("half_expand_comments_sheet", coroutineScope, sheetUiState) {
        coroutineScope.launch {
          commentsSheetState.partialExpand()
          // FIXME: this event is a workaround to inform the FSM that the sheet
          //  partially expanded until material3 fixes the bug of bottom sheet
          //  state changing from Hidden to PartiallyExpanded when passed to
          //  the scaffold.
          dispatch(v("stream_panel_fsm", "half_expand_comments_sheet"))
        }
      }
      RegFx("expand_comments_sheet", coroutineScope, sheetUiState) {
        coroutineScope.launch { commentsSheetState.expand() }
      }
      RegFx("close_comments_sheet", coroutineScope, sheetUiState) {
        coroutineScope.launch {
          commentsSheetState.hide()
          dispatch(v("nav_back_to_comments"))
        }
      }

      if (commentsListState == null) return@BottomSheetScaffold

      CommentsSheet(
        uiState = commentsListState,
        contentHeight = get<Dp>(sheetData, ":sheet_content_height")!!,
        isSheetOpen = get(sheetData, ":is_sheet_open", false)!!,
        toggleExpansion = onClickSheetHeader
      )
    },
    scaffoldState = commentsScaffoldState,
    sheetPeekHeight = get<Dp>(sheetData, ":sheet_peak_height")!!,
    sheetDragHandle = {
      DragHandle(onClick = onClickSheetHeader)
    },
    content = content
  )
}

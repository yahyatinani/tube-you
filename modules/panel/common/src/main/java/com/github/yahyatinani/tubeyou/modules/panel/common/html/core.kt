package com.github.yahyatinani.tubeyou.modules.panel.common.html

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue400

/**
 * Source: https://stackoverflow.com/questions/73989319/how-to-convert-spannable-to-annotatedstring-in-android
 */
fun Spanned.toAnnotatedString(): AnnotatedString {
  val builder = AnnotatedString.Builder(this.toString())
  SpanCopier.values().forEach { copier ->
    getSpans(0, length, copier.spanClass).forEach { span ->
      copier.copySpan(
        span,
        getSpanStart(span),
        getSpanEnd(span),
        builder
      )
    }
  }
  return builder.toAnnotatedString()
}

private enum class SpanCopier {
  URL {
    override val spanClass = URLSpan::class.java
    override fun copySpan(
      span: Any,
      start: Int,
      end: Int,
      destination: AnnotatedString.Builder
    ) {
      val urlSpan = span as URLSpan
      destination.addStringAnnotation(
        tag = "URL",
        annotation = urlSpan.url,
        start = start,
        end = end
      )
      destination.addStyle(
        style = SpanStyle(color = Blue400),
        start = start,
        end = end
      )
    }
  },
  FOREGROUND_COLOR {
    override val spanClass = ForegroundColorSpan::class.java
    override fun copySpan(
      span: Any,
      start: Int,
      end: Int,
      destination: AnnotatedString.Builder
    ) {
      val colorSpan = span as ForegroundColorSpan
      destination.addStyle(
        style = SpanStyle(color = Color(colorSpan.foregroundColor)),
        start = start,
        end = end
      )
    }
  },
  UNDERLINE {
    override val spanClass = UnderlineSpan::class.java
    override fun copySpan(
      span: Any,
      start: Int,
      end: Int,
      destination: AnnotatedString.Builder
    ) {
      destination.addStyle(
        style = SpanStyle(textDecoration = TextDecoration.Underline),
        start = start,
        end = end
      )
    }
  },
  STYLE {
    override val spanClass = StyleSpan::class.java
    override fun copySpan(
      span: Any,
      start: Int,
      end: Int,
      destination: AnnotatedString.Builder
    ) {
      val styleSpan = span as StyleSpan

      destination.addStyle(
        style = when (styleSpan.style) {
          Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
          Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
          Typeface.BOLD_ITALIC -> SpanStyle(
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
          )

          else -> SpanStyle()
        },
        start = start,
        end = end
      )
    }
  };

  abstract val spanClass: Class<out CharacterStyle>
  abstract fun copySpan(
    span: Any,
    start: Int,
    end: Int,
    destination: AnnotatedString.Builder
  )
}

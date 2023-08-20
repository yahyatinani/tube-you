package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.dash

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamData
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamDetails
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

// Based off of https://github.com/libre-tube/LibreTube/blob/master/app/src/
// main/java/com/github/libretube/helpers/DashHelper.kt

object DashHelper {

  private val builderFactory: DocumentBuilderFactory =
    DocumentBuilderFactory.newInstance()
  private val transformerFactory: TransformerFactory =
    TransformerFactory.newInstance()

  private data class AdapSetInfo(
    val mimeType: String,
    val audioTrackId: String? = null,
    val formats: MutableList<StreamDetails> = mutableListOf()
  )

  fun createManifest(
    streams: StreamData,
    supportsHdr: Boolean,
    audioOnly: Boolean = false,
    rewriteUrls: Boolean = false
  ): String {
    val builder = builderFactory.newDocumentBuilder()

    val doc = builder.newDocument()
    val mpd = doc.createElement("MPD")
    mpd.setAttribute("xmlns", "urn:mpeg:dash:schema:mpd:2011")
    mpd.setAttribute("profiles", "urn:mpeg:dash:profile:full:2011")
    mpd.setAttribute("minBufferTime", "PT1.5S")
    mpd.setAttribute("type", "static")
    mpd.setAttribute("mediaPresentationDuration", "PT${streams.duration}S")

    val period = doc.createElement("Period")

    val adapSetInfos = ArrayList<AdapSetInfo>()

    if (!audioOnly) {
      val enabledVideoCodecs = "all"
      for (
      stream in streams.videoStreams
        // used to avoid including LBRY HLS inside the streams in the manifest
        .filter { !it.format.orEmpty().contains("HLS") }
        // filter the codecs according to the user's preferences
        .filter {
          enabledVideoCodecs == "all" || it.codec.orEmpty().lowercase()
            .startsWith(
              enabledVideoCodecs
            )
        }
        .filter {
          supportsHdr || !it.quality.orEmpty().uppercase().contains("HDR")
        }
      ) {
        // ignore dual format streams
        if (!stream.videoOnly!!) {
          continue
        }

        // ignore streams which might be OTF
        if (stream.indexEnd!! <= 0) {
          continue
        }

        val adapSetInfo = adapSetInfos.find { it.mimeType == stream.mimeType }
        if (adapSetInfo != null) {
          adapSetInfo.formats.add(stream)
          continue
        }
        adapSetInfos.add(
          AdapSetInfo(
            stream.mimeType,
            null,
            mutableListOf(stream)
          )
        )
      }
    }

    for (stream in streams.audioStreams) {
      val adapSetInfo = adapSetInfos.find {
        it.mimeType == stream.mimeType &&
          it.audioTrackId == stream.audioTrackId
      }
      if (adapSetInfo != null) {
        adapSetInfo.formats.add(stream)
        continue
      }
      adapSetInfos.add(
        AdapSetInfo(
          stream.mimeType,
          stream.audioTrackId,
          mutableListOf(stream)
        )
      )
    }

    for (adapSet in adapSetInfos) {
      val adapSetElement = doc.createElement("AdaptationSet")
      adapSetElement.setAttribute("mimeType", adapSet.mimeType)
      adapSetElement.setAttribute("startWithSAP", "1")
      adapSetElement.setAttribute("subsegmentAlignment", "true")
      if (adapSet.audioTrackId != null) {
        adapSetElement.setAttribute(
          "lang",
          adapSet.audioTrackId.substring(0, 2)
        )
      }

      val isVideo = adapSet.mimeType.contains("video")

      if (isVideo) {
        adapSetElement.setAttribute("scanType", "progressive")
      }

      for (stream in adapSet.formats) {
        val rep = let {
          if (isVideo) {
            createVideoRepresentation(doc, stream, rewriteUrls)
          } else {
            createAudioRepresentation(doc, stream, rewriteUrls)
          }
        }
        adapSetElement.appendChild(rep)
      }

      period.appendChild(adapSetElement)
    }

    mpd.appendChild(period)

    doc.appendChild(mpd)

    val domSource = DOMSource(doc)
    val writer = StringWriter()

    val transformer = transformerFactory.newTransformer()
    transformer.transform(domSource, StreamResult(writer))

    return writer.toString()
  }

  private fun createAudioRepresentation(
    doc: Document,
    stream: StreamDetails,
    rewriteUrls: Boolean
  ): Element {
    val representation = doc.createElement("Representation")
    representation.setAttribute("bandwidth", stream.bitrate.toString())
    representation.setAttribute("codecs", stream.codec!!)
    representation.setAttribute("mimeType", stream.mimeType!!)

    val audioChannelConfiguration =
      doc.createElement("AudioChannelConfiguration")
    audioChannelConfiguration.setAttribute(
      "schemeIdUri",
      "urn:mpeg:dash:23003:3:audio_channel_configuration:2011"
    )
    audioChannelConfiguration.setAttribute("value", "2")

    val baseUrl = doc.createElement("BaseURL")
    baseUrl.appendChild(doc.createTextNode(stream.url!!))

    val segmentBase = doc.createElement("SegmentBase")
    segmentBase.setAttribute(
      "indexRange",
      "${stream.indexStart}-${stream.indexEnd}"
    )

    val initialization = doc.createElement("Initialization")
    initialization.setAttribute(
      "range",
      "${stream.initStart}-${stream.initEnd}"
    )
    segmentBase.appendChild(initialization)

    representation.appendChild(audioChannelConfiguration)
    representation.appendChild(baseUrl)
    representation.appendChild(segmentBase)

    return representation
  }

  private fun createVideoRepresentation(
    doc: Document,
    stream: StreamDetails,
    rewriteUrls: Boolean
  ): Element {
    val representation = doc.createElement("Representation")
    representation.setAttribute("codecs", stream.codec!!)
    representation.setAttribute("bandwidth", stream.bitrate.toString())
    representation.setAttribute("width", stream.width.toString())
    representation.setAttribute("height", stream.height.toString())
    representation.setAttribute("maxPlayoutRate", "1")
    representation.setAttribute("frameRate", stream.fps.toString())

    val baseUrl = doc.createElement("BaseURL")
    baseUrl.appendChild(doc.createTextNode(stream.url))

    val segmentBase = doc.createElement("SegmentBase")
    segmentBase.setAttribute(
      "indexRange",
      "${stream.indexStart}-${stream.indexEnd}"
    )

    val initialization = doc.createElement("Initialization")
    initialization.setAttribute(
      "range",
      "${stream.initStart}-${stream.initEnd}"
    )
    segmentBase.appendChild(initialization)

    representation.appendChild(baseUrl)
    representation.appendChild(segmentBase)

    return representation
  }
}

fun createDashSource(streamData: StreamData, context: Context): Uri {
  val manifest = DashHelper.createManifest(
    streamData,
    DisplayHelper.supportsHdr(context)
  )

  // encode to base64
  val encoded = Base64.encodeToString(manifest.toByteArray(), Base64.DEFAULT)
  return "data:application/dash+xml;charset=utf-8;base64,$encoded".toUri()
}

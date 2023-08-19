package io.github.yahyatinani.tubeyou.modules.core.network

import kotlinx.serialization.modules.SerializersModule

val searchModule = SerializersModule {
  polymorphic(
    baseClass = Searchable::class,
    actualClass = Video::class,
    actualSerializer = Video.serializer()
  )
  polymorphic(
    baseClass = Searchable::class,
    actualClass = Channel::class,
    actualSerializer = Channel.serializer()
  )
  polymorphic(
    baseClass = Searchable::class,
    actualClass = Playlist::class,
    actualSerializer = Playlist.serializer()
  )
}

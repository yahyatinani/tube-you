package io.github.yahyatinani.tubeyou

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.io.FileOutputStream
import java.util.Base64

object TyBuild {
  const val APP_ID = "io.github.yahyatinani.tubeyou"
  const val APP_NAME = "TubeYou"

  const val versionMajor = 0
  const val versionMinor = 0
  const val versionPatch = 1

  object Versions {
    const val KOTLIN = "1.8"
    const val JVM = "11"
  }
}

fun keyStoreBase64ToStoreFile(keyStoreBase64: String?): File? {
  if (keyStoreBase64 == null) return null

  val tempKeyStoreFile = File.createTempFile("tmp_ks_", ".jks")
  var fos: FileOutputStream? = null
  try {
    fos = FileOutputStream(tempKeyStoreFile)
    fos.write(Base64.getDecoder().decode(keyStoreBase64))
    fos.flush()
  } finally {
    fos?.close()
  }

  return tempKeyStoreFile
}

val Project.deps
  get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>()
    .named("deps")

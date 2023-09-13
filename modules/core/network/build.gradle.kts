import io.github.yahyatinani.tubeyou.TyBuild

plugins {
  id("tubeyou.android.library")
  id("kotlinx-serialization")
}

android {
  namespace = "${TyBuild.APP_ID}.modules.core.network"
}

dependencies {
  implementation(deps.kotlinx.serialization.json)
  implementation(deps.recompose.pagingfx)
  implementation(deps.ktor.client.encoding)
}

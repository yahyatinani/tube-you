import io.github.yahyatinani.tubeyou.TyBuild

plugins {
  id("tubeyou.android.feature")
  id("tubeyou.android.library.compose")
}

android {
  namespace = "${TyBuild.APP_ID}.modules.feature.watch"
}

dependencies {
  implementation(deps.androidx.media3.exoplayer)
  implementation(deps.androidx.media3.exoplayer.hls)
  implementation(deps.androidx.media3.exoplayer.dash)
  implementation(deps.androidx.media3.datasource.cronet)
  implementation(deps.androidx.media3.ui)
  implementation(deps.androidx.media3.session)
  implementation(deps.cronet.okhttp)
  implementation(deps.kotlinx.datetime)
}
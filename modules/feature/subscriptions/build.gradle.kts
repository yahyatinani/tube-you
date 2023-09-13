import io.github.yahyatinani.tubeyou.TyBuild

plugins {
  id("tubeyou.android.feature")
  id("tubeyou.android.library.compose")
}

android {
  namespace = "${TyBuild.APP_ID}.modules.feature.subscriptions"
}

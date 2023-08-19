import io.github.yahyatinani.tubeyou.TyBuild

plugins {
  id("tubeyou.android.library")
  id("tubeyou.android.library.compose")
}

android {
  namespace = "${TyBuild.APP_ID}.modules.core.ui"
}

dependencies {
  api(deps.compose.runtime)
  api(project(":modules:core:viewmodels"))

  implementation(deps.androidx.compose.ui)
}

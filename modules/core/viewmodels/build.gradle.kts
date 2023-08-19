import io.github.yahyatinani.tubeyou.TyBuild

plugins {
  id("tubeyou.android.library")
  id("tubeyou.android.library.compose")
}

android {
  namespace = "${TyBuild.APP_ID}.modules.core.viewmodels"
}

dependencies {
  api(project(":modules:core:designsystem"))
  api(deps.compose.runtime)

  implementation(project(":modules:core:network"))
  implementation(deps.androidx.compose.ui)
}

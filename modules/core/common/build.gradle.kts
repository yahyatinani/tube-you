import io.github.yahyatinani.tubeyou.TyBuild

plugins {
  id("tubeyou.android.library")
}

android {
  namespace = "${TyBuild.APP_ID}.modules.core.common"
}

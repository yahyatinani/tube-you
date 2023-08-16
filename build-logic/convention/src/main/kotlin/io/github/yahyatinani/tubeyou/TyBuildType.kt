package io.github.yahyatinani.tubeyou

import io.github.yahyatinani.tubeyou.TyBuild.APP_NAME

enum class TyBuildType(
  val applicationIdSuffix: String? = null,
  val versionNameSuffix: String? = null,
  val applicationName: String = APP_NAME
) {
  DEBUG(".debug", "-debug", "$APP_NAME-debug"),
  RELEASE,
  BENCHMARK(".benchmark", "-benchmark", "$APP_NAME-benchmark")
}

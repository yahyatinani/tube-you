import io.github.yahyatinani.tubeyou.TyBuild

plugins {
  id("tubeyou.android.library")
  id("tubeyou.android.library.compose")
}

android {
  namespace = "${TyBuild.APP_ID}.modules.designsystem"

  defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  lint {
    checkDependencies = true
  }
}

dependencies {
  api(project(":modules:core:keywords"))
  api(deps.compose.foundation)
  api(deps.compose.foundation.layout)
  api(deps.compose.material.iconsExtended)
  api(deps.compose.material3)
  api(deps.compose.material3.windowSizeClass)
  api(deps.compose.runtime)
  api(deps.compose.ui.tooling.preview)
  api(deps.compose.ui.util)
  api(deps.accompanist.swiperefresh)

  debugApi(deps.compose.ui.tooling)

  implementation(deps.core.ktx)
  implementation(deps.coil.kt.compose)

  /*  implementation project(':app:core:keywords')
    implementation deps.bundles.compose
    implementation deps.coil
    implementation deps.y.core
    implementation deps.kotlinx.datetime

    testImplementation deps.kotlin.reflect
    testImplementation deps.kotest.jvm.runner
    testImplementation deps.kotest.assertions*/
}

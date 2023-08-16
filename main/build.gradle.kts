import io.github.yahyatinani.tubeyou.TyBuild
import io.github.yahyatinani.tubeyou.TyBuildType

plugins {
  id("tubeyou.android.application")
  id("tubeyou.android.application.compose")
}

android {
  namespace = TyBuild.APP_ID

  defaultConfig {
    applicationId = TyBuild.APP_ID
    versionCode = 1
    versionName = "0.0.1" // X.Y.Z; X = Major, Y = minor, Z = Patch level

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    debug {
      applicationIdSuffix = TyBuildType.DEBUG.applicationIdSuffix
      versionNameSuffix = TyBuildType.DEBUG.versionNameSuffix

      resValue(
        type = "string",
        name = "app_version",
        value = "${defaultConfig.versionName}$versionNameSuffix"
      )

      resValue(
        type = "string",
        name = "app_name",
        value = TyBuildType.DEBUG.applicationName
      )
    }
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      applicationIdSuffix = TyBuildType.RELEASE.applicationIdSuffix

      resValue(
        type = "string",
        name = "app_version",
        value = "${defaultConfig.versionName}"
      )
      resValue(
        type = "string",
        name = "app_name",
        value = TyBuildType.RELEASE.applicationName
      )

      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(project(":modules:core:designsystem"))

  implementation(deps.core.ktx)
  implementation(deps.lifecycle.runtime.ktx)
  implementation(deps.activity.compose)
  implementation(deps.compose.material3)
  implementation(deps.androidx.navigation.compose)
  testImplementation(deps.junit)
  androidTestImplementation(deps.androidx.test.ext.junit)
//  androidTestImplementation(deps.espresso.core)
  androidTestImplementation(deps.compose.ui.test.junit4)
  debugImplementation(deps.compose.ui.tooling)
  debugImplementation(deps.compose.ui.test.manifest)
}
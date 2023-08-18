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
    val release by getting {
      isMinifyEnabled = true
      isShrinkResources = true
      applicationIdSuffix = TyBuildType.RELEASE.applicationIdSuffix
      signingConfig = signingConfigs.getByName("debug")

      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )

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
    }

    create("benchmark") {
      // Enable all the optimizations from release build via initWith(release).
      initWith(release)
      matchingFallbacks.add("release")
      // Debug key signing is available to everyone.
      signingConfig = signingConfigs.getByName("debug")
      // Only use benchmark proguard rules
      proguardFiles("benchmark-rules.pro")
      isMinifyEnabled = true
      applicationIdSuffix = TyBuildType.BENCHMARK.applicationIdSuffix
    }
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(project(":modules:feature:home"))
  implementation(project(":modules:feature:subscriptions"))
  implementation(project(":modules:feature:library"))
  implementation(project(":modules:core:designsystem"))

  testImplementation(deps.junit)
  androidTestImplementation(deps.androidx.test.ext.junit)
  androidTestImplementation(deps.compose.ui.test.junit4)
  debugImplementation(deps.compose.ui.tooling)
  debugImplementation(deps.compose.ui.test.manifest)
  debugImplementation(deps.leakcanary.android)

  implementation(deps.core.ktx)
  implementation(deps.androidx.core.splashscreen)
  implementation(deps.lifecycle.runtime.ktx)
  implementation(deps.activity.compose)
  implementation(deps.compose.material3)
  implementation(deps.androidx.navigation.compose)
  implementation(deps.androidx.profileinstaller)
}
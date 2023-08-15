import io.github.yahyatinani.tubeyou.TyBuild

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
    release {
      isMinifyEnabled = false
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
  implementation(deps.core.ktx)
  implementation(deps.lifecycle.runtime.ktx)
  implementation(deps.activity.compose)
//  implementation(deps.ui)
//  implementation(deps.ui.graphics)
//  implementation(deps.ui.tooling.preview)
  implementation(deps.material3)
  testImplementation(deps.junit)
  androidTestImplementation(deps.androidx.test.ext.junit)
//  androidTestImplementation(deps.espresso.core)
  androidTestImplementation(deps.ui.test.junit4)
  debugImplementation(deps.ui.tooling)
  debugImplementation(deps.ui.test.manifest)
}
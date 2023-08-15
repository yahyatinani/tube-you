@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(deps.plugins.androidApplication)
  alias(deps.plugins.kotlinAndroid)
//  id("tubeyou.android-app-conventions")
}

android {
  namespace = "com.github.yahyatinani.tubeyou"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.github.yahyatinani.tubeyou"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

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
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.4.7"
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
  implementation(platform(deps.compose.bom))
  implementation(deps.ui)
  implementation(deps.ui.graphics)
  implementation(deps.ui.tooling.preview)
  implementation(deps.material3)
  testImplementation(deps.junit)
  androidTestImplementation(deps.androidx.test.ext.junit)
  androidTestImplementation(deps.espresso.core)
  androidTestImplementation(platform(deps.compose.bom))
  androidTestImplementation(deps.ui.test.junit4)
  debugImplementation(deps.ui.tooling)
  debugImplementation(deps.ui.test.manifest)

  coreLibraryDesugaring(deps.android.tools.desugar)
}
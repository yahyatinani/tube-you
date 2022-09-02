import com.github.whyrising.vancetube.Build.APP_ID
import com.github.whyrising.vancetube.Build.Versions.COMPOSE_COMPILER
import com.github.whyrising.vancetube.Build.keyStoreBase64ToStoreFile
import com.github.whyrising.vancetube.Build.versionMajor
import com.github.whyrising.vancetube.Build.versionMinor
import com.github.whyrising.vancetube.Build.versionPatch
import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
  id("kotlin-conventions")
  id("com.android.application")
}

android {
  compileSdk = 33

  defaultConfig {
    applicationId = APP_ID
    minSdk = 23
    targetSdk = 33
    versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
    versionName = "$versionMajor.$versionMinor.$versionPatch"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  signingConfigs {
    create("release") {
      storeFile = keyStoreBase64ToStoreFile(System.getenv("SIGNING_KEY_BASE64"))
      storePassword = System.getenv("KEYSTORE_PASSWORD")
      keyAlias = System.getenv("KEY_ALIAS")
      keyPassword = System.getenv("KEY_PASSWORD")
    }
  }

  buildTypes {
    debug {
      isMinifyEnabled = false
      versionNameSuffix = "-debug"
      applicationIdSuffix = ".debug"
      resValue(
        type = "string",
        name = "app_version",
        value = "${defaultConfig.versionName}$versionNameSuffix"
      )
      if (System.getenv("SIGNING_KEY_BASE64") != null)
        signingConfig = signingConfigs.getByName("debug")
    }
    release {
      //isDebuggable = true // TODO: Remove when done
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      resValue(
        type = "string",
        name = "app_version",
        value = "${defaultConfig.versionName}"
      )
      signingConfig = signingConfigs.getByName("release")
    }
  }

  packagingOptions {
    resources {
      excludes += setOf("/*.jar", "/META-INF/{AL2.0,LGPL2.1}")
    }
  }

  buildFeatures {
    compose = true
  }


  composeOptions {
    kotlinCompilerExtensionVersion = COMPOSE_COMPILER
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
  }

  testOptions {
    animationsDisabled = true
    unitTests.isReturnDefaultValues = true
  }

  publishing {
    singleVariant("release") {
      withSourcesJar()
      withJavadocJar()
    }
  }
}

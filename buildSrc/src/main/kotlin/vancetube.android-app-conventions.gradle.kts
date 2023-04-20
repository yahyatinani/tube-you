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
  namespace = APP_ID

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
    val appName = "TubeYou"
    val debug by getting {
      isMinifyEnabled = false
      versionNameSuffix = "-debug"
      resValue(
        type = "string",
        name = "app_version",
        value = "${defaultConfig.versionName}$versionNameSuffix"
      )
      resValue(
        type = "string",
        name = "app_name",
        value = "$appName-debug"
      )
      if (System.getenv("SIGNING_KEY_BASE64") != null)
        signingConfig = signingConfigs.getByName("debug")
    }

    val release by getting {
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
      resValue(
        type = "string",
        name = "app_name",
        value = "$appName"
      )
      signingConfig = signingConfigs.getByName("release")
    }

    val benchmark by creating {
      initWith(release)
      matchingFallbacks.add("release")
      signingConfig = signingConfigs.getByName("debug")
      // Only use benchmark proguard rules
      proguardFiles("benchmark-rules.pro")
      isDebuggable = false
    }
  }

  packaging {
    resources {
      excludes += setOf(
        "/*.jar",
        "/META-INF/{AL2.0,LGPL2.1}",
        "META-INF/INDEX.LIST"
      )
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

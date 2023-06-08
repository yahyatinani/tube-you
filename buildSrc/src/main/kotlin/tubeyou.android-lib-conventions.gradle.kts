import com.github.yahyatinani.tubeyou.Build

plugins {
  id("kotlin-conventions")
  id("com.android.library")
}

//group = APP_ID

android {
//  namespace = APP_ID
  compileSdk = 33

  buildFeatures {
    compose = true
  }

  defaultConfig {
    minSdk = 23
    targetSdk = 33
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
    aarMetadata {
      minCompileSdk = 23
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }

  packagingOptions {
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
    kotlinCompilerExtensionVersion = Build.Versions.COMPOSE_COMPILER
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  testOptions {
    unitTests.isReturnDefaultValues = true
  }

  publishing {
    singleVariant("release") {
      withSourcesJar()
      withJavadocJar()
    }
  }
}

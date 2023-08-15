import com.android.build.api.dsl.ManagedVirtualDevice

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(deps.plugins.androidx.baselineprofile)
  id("com.android.test")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.github.yahyatinani.tubeyou.baselineprofile"
  compileSdk = 34

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }

  defaultConfig {
    minSdk = 28
    targetSdk = 34

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  targetProjectPath = ":app:main"

/*  testOptions.managedDevices.devices {
    create<ManagedVirtualDevice>("pixel6Api34") {
      device = "Pixel 6"
      apiLevel = 34
      systemImageSource = "google"
    }
  }*/
}

// This is the configuration block for the Baseline Profile plugin.
// You can specify to run the generators on a managed devices or connected devices.
baselineProfile {
//  managedDevices += "pixel6Api34"
//  useConnectedDevices = false
}

dependencies {
  implementation(deps.junit)
  implementation(deps.espresso.core)
  implementation(deps.uiautomator)
  implementation(deps.benchmark.macro.junit4)
}
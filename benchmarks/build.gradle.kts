import io.github.yahyatinani.tubeyou.TyBuild
import io.github.yahyatinani.tubeyou.TyBuildType

plugins {
  id("tubeyou.android.test")
}

android {
  namespace = "${TyBuild.APP_ID}.benchmarks"

  defaultConfig {
    minSdk = 28
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    buildConfigField("String", "APP_BUILD_TYPE_SUFFIX", "\"\"")
  }

  buildFeatures {
    buildConfig = true
  }

  buildTypes {
    // This benchmark buildType is used for benchmarking, and should function like your
    // release build (for example, with minification on). It's signed with a debug key
    // for easy local/CI testing.
    create("benchmark") {
      // Keep the build type debuggable so we can attach a debugger if needed.
      isDebuggable = true
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks.add("release")
      buildConfigField(
        "String",
        "APP_BUILD_TYPE_SUFFIX",
        "\"${TyBuildType.BENCHMARK.applicationIdSuffix ?: ""}\""
      )
    }
  }

  targetProjectPath = ":main"
  experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
  implementation(deps.androidx.benchmark.macro)
  implementation(deps.androidx.test.core)
  implementation(deps.androidx.test.espresso.core)
  implementation(deps.androidx.test.ext)
  implementation(deps.androidx.test.rules)
  implementation(deps.androidx.test.runner)
  implementation(deps.androidx.test.uiautomator)
}

androidComponents {
  beforeVariants {
    it.enable = it.buildType == "benchmark"
  }
}

import com.github.whyrising.vancetube.Build
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("android")
}

tasks.withType<Test> {
  useJUnitPlatform()
  val decimal = Runtime.getRuntime().availableProcessors() / 2
  maxParallelForks = if (decimal > 0) decimal else 1
  filter {
    isFailOnNoMatchingTests = false
  }
  testLogging {
    exceptionFormat = TestExceptionFormat.FULL
    events = setOf(
      TestLogEvent.SKIPPED,
      TestLogEvent.FAILED,
      TestLogEvent.STANDARD_OUT,
      TestLogEvent.STANDARD_ERROR
    )
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    val buildDirAbsolutePath = project.buildDir.absolutePath
    val out = "/compose_metrics"
    val composePlugin = "plugin:androidx.compose.compiler.plugins.kotlin"
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-P",
      "$composePlugin:reportsDestination=$buildDirAbsolutePath$out",
      "-P",
      "$composePlugin:metricsDestination=$buildDirAbsolutePath$out"
    )
    freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"

    jvmTarget = Build.Versions.JVM
    apiVersion = Build.Versions.KOTLIN
    languageVersion = Build.Versions.KOTLIN
  }
}

kotlin {
  sourceSets {
    all {
      languageSettings.optIn("kotlin.time.ExperimentalTime")
      languageSettings.optIn("kotlin.experimental.ExperimentalTypeInference")
      languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
    }
  }
}

import io.github.yahyatinani.tubeyou.deps
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply {
        apply("tubeyou.android.library")
      }
      dependencies {
        add(
          "implementation",
          deps.findLibrary("androidx.navigation.compose").get()
        )

        add("implementation", project(":modules:core:designsystem"))
        /*        add("implementation", project(":core:model"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:data"))
                add("implementation", project(":core:common"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:analytics"))

                add("testImplementation", kotlin("test"))
                add("testImplementation", project(":core:testing"))
                add("androidTestImplementation", kotlin("test"))
                add("androidTestImplementation", project(":core:testing"))

                add("implementation", deps.findLibrary("coil.kt").get())
                add("implementation", deps.findLibrary("coil.kt.compose").get())


                add(
                  "implementation",
                  deps.findLibrary("androidx.lifecycle.runtimeCompose").get()
                )
                add(
                  "implementation",
                  deps.findLibrary("androidx.lifecycle.viewModelCompose").get()
                )

                add(
                  "implementation",
                  deps.findLibrary("kotlinx.coroutines.android").get()
                )*/
      }
    }
  }
}

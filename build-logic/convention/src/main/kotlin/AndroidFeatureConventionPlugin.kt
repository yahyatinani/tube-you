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

        add("implementation", project(":modules:core:common"))
        add("implementation", project(":modules:core:network"))
        add("implementation", project(":modules:core:designsystem"))
        add("implementation", project(":modules:core:viewmodels"))
        add("implementation", project(":modules:core:ui"))
        add("implementation", deps.findLibrary("recompose-httpfx").get())
        add("implementation", deps.findLibrary("recompose-fsm").get())
        add("api", deps.findLibrary("recompose-pagingfx").get())
      }
    }
  }
}

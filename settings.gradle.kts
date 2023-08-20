pluginManagement {
  includeBuild("build-logic")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  defaultLibrariesExtensionName.set("deps")
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven {
      setUrl(
        "https://s01.oss.sonatype.org/content/repositories/snapshots/"
      )
    }
    maven {
      setUrl(
        "https://oss.sonatype.org/content/repositories/snapshots/"
      )
    }
  }
}

rootProject.name = "TubeYou"

include(":main")
include(":benchmarks")
include(":modules:feature:watch")
include(":modules:feature:search")
include(":modules:feature:home")
include(":modules:feature:subscriptions")
include(":modules:feature:you")
include(":modules:core:common")
include(":modules:core:designsystem")
include(":modules:core:network")
include(":modules:core:keywords")
include(":modules:core:viewmodels")
include(":modules:core:ui")

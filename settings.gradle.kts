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
include(":modules:feature:home")
include(":modules:feature:subscriptions")
include(":modules:feature:library")
include(":modules:core:designsystem")
include(":modules:core:keywords")

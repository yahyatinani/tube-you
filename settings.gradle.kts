pluginManagement {
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

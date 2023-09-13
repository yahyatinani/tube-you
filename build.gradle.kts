plugins {
  alias(deps.plugins.spotless)
  alias(deps.plugins.androidApplication) apply false
  alias(deps.plugins.kotlin.jvm) apply false
  alias(deps.plugins.kotlin.serialization) apply false
}

spotless {
  groovyGradle {
    target("**/*.gradle")
    trimTrailingWhitespace()
    endWithNewline()
    greclipse().configFile("./greclipse.properties")
  }
  val config = mapOf(
    "indent_size" to "2",
    "max_line_length" to "80",
    "indent_style" to "space",
    "charset" to "utf-8",
    "end_of_line" to "lf",
    "disabled_rules" to "filename,enum-entry-name-case,annotation",
    "trim_trailing_whitespace" to "true",
    "insert_final_newline" to "true"
  )
  kotlin {
    target("**/*.kt")
    targetExclude("buildSrc/build/")
    ktlint().editorConfigOverride(config)

    trimTrailingWhitespace()
    endWithNewline()
  }
  kotlinGradle {
    ktlint().editorConfigOverride(config)
  }
}

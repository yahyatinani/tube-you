package io.github.yahyatinani.tubeyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import io.github.yahyatinani.tubeyou.ui.TyApp

/*
 * Profiling:
 *
 * Java: ~ 9.4 MB
 * Total: ~ 88.2 MB
 */

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      TyTheme {
        TyApp()
      }
    }
  }
}

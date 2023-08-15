package io.github.yahyatinani.tubeyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import io.github.yahyatinani.tubeyou.theme.ui.TyTheme

/*
 * Profiling:
 *
 * Java: ~ 9.4 MB
 * Total: ~ 88.2 MB
 */

enum class NavItems(val unselectedIcon: ImageVector) {
  Home(unselectedIcon = Icons.Default.Home),
  Subs(unselectedIcon = Icons.Default.Settings),
  Library(unselectedIcon = Icons.Default.MailOutline)
}

val navItems = NavItems.values().asList()

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TyTheme {
        Scaffold(
          bottomBar = {
            NavigationBar {
              navItems.forEach {
                NavigationBarItem(
                  selected = false,
                  onClick = { /*TODO*/ },
                  icon = {
                    Icon(
                      imageVector = it.unselectedIcon,
                      contentDescription = null
                    )
                  }
                )
              }
            }
          }
        ) {
          Surface(
            modifier = Modifier
              .fillMaxSize()
              .padding(it),
            color = MaterialTheme.colorScheme.background
          ) {
            Greeting("Android")
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  TyTheme {
    Greeting("Android")
  }
}

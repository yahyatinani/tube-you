package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fx

import android.app.Activity
import android.content.pm.ActivityInfo
import android.provider.Settings
import android.view.OrientationEventListener

class TyOrientationEventListener(private val context: Activity) :
  OrientationEventListener(context) {
  private fun epsilonCheck(
    orientation: Int,
    degree: Int,
    epsilon: Int
  ): Boolean {
    return orientation > degree - epsilon && orientation < degree + epsilon
  }

  private fun epsilonCheckP(
    orientation: Int,
    degree: Int,
    epsilon: Int
  ): Boolean {
    return orientation > degree - epsilon && orientation < degree + epsilon
  }

  private fun isAutoRotate() = Settings.System.getInt(
    context.contentResolver,
    Settings.System.ACCELEROMETER_ROTATION,
    0
  ) == 1

  override fun onOrientationChanged(orientation: Int) {
    val epsilon = 5
    val leftLandscape = 90
    val rightLandscape = 270
    val currentOrientation = context.requestedOrientation

    if (isAutoRotate()) {
      if (epsilonCheck(orientation, 0, epsilon) ||
        epsilonCheck(orientation, 180, epsilon)
      ) {
        // portrait
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
          context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
      } else if (epsilonCheck(orientation, leftLandscape, epsilon) ||
        epsilonCheck(orientation, rightLandscape, epsilon)
      ) {
        // landscape

        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
          context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
      }
    } else {
      context.requestedOrientation = currentOrientation
    }
  }
}

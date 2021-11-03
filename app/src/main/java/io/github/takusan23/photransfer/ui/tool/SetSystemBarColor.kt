package io.github.takusan23.photransfer.ui

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.ln

/**
 * Material3のSurfaceの色をステータスバーとナビゲーションバーに適用する。
 *
 * @param color colorScheme.surface以外がいい場合はどうぞ
 * @param isNavigationBar ナビゲーションバーに適用する場合はtrue
 * @param isStatusBar ステータスバーに適用する場合はtrue
 * */
@Composable
fun SetSystemBarColor(
    color: Color = MaterialTheme.colorScheme.surface,
    tonalElevation: Dp = 0.dp,
    isStatusBar: Boolean = true,
    isNavigationBar: Boolean = false,
) {
    val context = LocalContext.current
    val applyColor = if (color == MaterialTheme.colorScheme.surface) {
        MaterialTheme.colorScheme.applyTonalElevation(LocalAbsoluteTonalElevation.current + tonalElevation)
    } else {
        color
    }
    // 変化したら適用
    LaunchedEffect(key1 = applyColor, key2 = isStatusBar, key3 = isNavigationBar, block = {
        val legacyColor = applyColor.toArgb()
        if (isStatusBar) {
            (context as? Activity)?.window?.statusBarColor = android.graphics.Color.argb(
                legacyColor.alpha,
                legacyColor.red,
                legacyColor.green,
                legacyColor.blue,
            )
        }
        if (isNavigationBar) {
            (context as? Activity)?.window?.navigationBarColor = android.graphics.Color.argb(
                legacyColor.alpha,
                legacyColor.red,
                legacyColor.green,
                legacyColor.blue,
            )
        }
    })
}

private fun ColorScheme.applyTonalElevation(elevation: Dp): Color {
    if (elevation == 0.dp) return surface
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return primary.copy(alpha = alpha).compositeOver(surface)
}
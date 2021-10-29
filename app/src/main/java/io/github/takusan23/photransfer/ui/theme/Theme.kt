package io.github.takusan23.photransfer.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * テーマ
 *
 * @param isDynamicColor ダイナミックカラーを使う場合はtrue
 * */
@SuppressLint("NewApi")
@Composable
fun PhoTransferTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = false,
    content: @Composable() () -> Unit,
) {
    val isAndroidSLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current

    val colors = when {
        darkTheme && isDynamicColor && isAndroidSLater -> dynamicDarkColorScheme(context = context)
        !darkTheme && isDynamicColor && isAndroidSLater -> dynamicLightColorScheme(context = context)
        darkTheme -> darkColorScheme(
            primary = Color(0xFF006e29),
            secondary = Color(0xFF516351),
            tertiary = Color(0xFF39656c)
        )
        else -> lightColorScheme(
            primary = Color(0xFF006e29),
            secondary = Color(0xFF516351),
            tertiary = Color(0xFF39656c)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
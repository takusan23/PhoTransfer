package io.github.takusan23.photransfer.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.photransfer.R

/** サーバー見つからんときに使う */
@Composable
fun ServerNotFoundInfo() {
    HomeScreenItem(
        text = stringResource(id = R.string.not_found_device),
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.error,
        shape = RoundedCornerShape(20.dp),
        icon = painterResource(id = R.drawable.ic_outline_error_outline_24)
    )
}
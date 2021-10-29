package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.photransfer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigation: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) })
        },
        content = {
            Column(modifier = Modifier.padding(it)) {

                Button(
                    modifier = Modifier.padding(5.dp),
                    onClick = { onNavigation(NavigationLinkList.ServerScreen) },
                    content = { Text(text = "サーバー") }
                )
                
                Button(
                    modifier = Modifier.padding(5.dp),
                    onClick = { onNavigation(NavigationLinkList.ClientScreen) },
                    content = { Text(text = "クライアント") }
                )

            }
        }
    )
}
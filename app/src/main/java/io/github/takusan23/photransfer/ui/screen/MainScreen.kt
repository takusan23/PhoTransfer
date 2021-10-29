package io.github.takusan23.photransfer.ui.screen

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.takusan23.photransfer.ui.theme.PhoTransferTheme

/**
 * Composeでのエントリーポイント。最初に表示される画面
 * */
@Composable
fun MainScreen() {

    PhoTransferTheme {
        Surface {

            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = NavigationLinkList.HomeScreen) {
                composable(NavigationLinkList.HomeScreen) {
                    HomeScreen(onNavigation = { navController.navigate(it) })
                }
                composable(NavigationLinkList.ClientScreen) {
                    ClientScreen()
                }
                composable(NavigationLinkList.ServerScreen) {
                    ServerScreen()
                }
            }

        }
    }
}

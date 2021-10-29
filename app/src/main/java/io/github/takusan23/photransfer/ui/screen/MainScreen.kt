package io.github.takusan23.photransfer.ui.screen

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.github.takusan23.photransfer.ui.screen.setting.ClientSettingScreen
import io.github.takusan23.photransfer.ui.screen.setting.ServerSettingScreen
import io.github.takusan23.photransfer.ui.theme.PhoTransferTheme

/**
 * 最初に表示される画面
 *
 * ルーティングもここ
 * */
@Composable
fun MainScreen() {

    PhoTransferTheme {
        Surface {

            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = NavigationLinkList.HomeScreen) {
                // ホーム画面？
                composable(NavigationLinkList.HomeScreen) {
                    HomeScreen(onNavigation = { navController.navigate(it) })
                }
                // 初期設定画面
                navigation(route = NavigationLinkList.SetupScreen, startDestination = NavigationLinkList.SelectSetupScreen) {
                    composable(NavigationLinkList.SelectSetupScreen) {
                        // サーバー or クライアント
                        SelectSetupScreen(onNavigate = { navController.navigate(it) })
                    }
                    composable(NavigationLinkList.ClientSetupScreen) {
                        // クライアント設定画面。PopUpToでこの画面に戻れないようにする
                        ClientSettingScreen(
                            onNavigate = { navController.navigate(it, navOptions { popUpTo(NavigationLinkList.HomeScreen) { inclusive = true } }) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(NavigationLinkList.ServerSetupScreen) {
                        // サーバー側設定画面
                        ServerSettingScreen(
                            onNavigate = { navController.navigate(it, navOptions { popUpTo(NavigationLinkList.HomeScreen) { inclusive = true } }) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

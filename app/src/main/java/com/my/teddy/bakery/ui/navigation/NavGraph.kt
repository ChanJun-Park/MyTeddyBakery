package com.my.teddy.bakery.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.my.teddy.bakery.ui.screens.bakery.BakeryScreen
import com.my.teddy.bakery.ui.screens.result.ResultScreen
import com.my.teddy.bakery.ui.screens.baking.BakingGameScreen

/**
 * 앱의 네비게이션 경로 정의
 */
sealed class Screen(val route: String) {
    object Bakery : Screen("bakery")
    object BakingGame : Screen("baking_game")
    object Result : Screen("result/{accuracy}/{coinsEarned}") {
        fun createRoute(accuracy: Float, coinsEarned: Int): String {
            return "result/$accuracy/$coinsEarned"
        }
    }
}

/**
 * 앱의 네비게이션 그래프
 * 
 * 모든 화면과 화면 간 전환을 정의
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Bakery.route
    ) {
        // 메인 베이커리 화면
        composable(Screen.Bakery.route) {
            BakeryScreen(
                onStartBaking = {
                    navController.navigate(Screen.BakingGame.route)
                }
            )
        }
        
        // 빵 만들기 게임 화면
        composable(Screen.BakingGame.route) {
            BakingGameScreen(
                onGameComplete = { accuracy, coinsEarned ->
                    navController.navigate(
                        Screen.Result.createRoute(accuracy, coinsEarned)
                    ) {
                        // 게임 화면을 백스택에서 제거
                        popUpTo(Screen.BakingGame.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        // 결과 화면
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("accuracy") { type = NavType.FloatType },
                navArgument("coinsEarned") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val accuracy = backStackEntry.arguments?.getFloat("accuracy") ?: 0f
            val coinsEarned = backStackEntry.arguments?.getInt("coinsEarned") ?: 0
            
            ResultScreen(
                accuracy = accuracy,
                coinsEarned = coinsEarned,
                onPlayAgain = {
                    navController.navigate(Screen.BakingGame.route) {
                        popUpTo(Screen.Bakery.route)
                    }
                },
                onBackToBakery = {
                    navController.navigate(Screen.Bakery.route) {
                        popUpTo(Screen.Bakery.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

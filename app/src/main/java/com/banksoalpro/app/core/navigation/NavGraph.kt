package com.banksoalpro.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.banksoalpro.app.BankSoalApplication
import com.banksoalpro.app.feature.billing.SubscriptionScreen
import com.banksoalpro.app.feature.catalog.CatalogScreen
import com.banksoalpro.app.feature.exam.ExamScreen
import com.banksoalpro.app.feature.exam.ExamViewModel
import com.banksoalpro.app.feature.home.HomeScreen
import com.banksoalpro.app.feature.result.ResultScreen
import com.banksoalpro.app.feature.review.ReviewScreen
import com.banksoalpro.app.feature.settings.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.Catalog.createRoute(categoryId))
                },
                onNavigateToPremium = {
                    navController.navigate(Screen.Subscription.route)
                }
            )
        }

        composable(
            route = Screen.Catalog.route,
            arguments = listOf(
                navArgument(Screen.Catalog.ARG_CATEGORY_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString(Screen.Catalog.ARG_CATEGORY_ID) ?: ""
            CatalogScreen(
                categoryId = categoryId,
                onPackageClick = { packageId ->
                    navController.navigate(Screen.Exam.createRoute(packageId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Exam.route,
            arguments = listOf(
                navArgument(Screen.Exam.ARG_PACKAGE_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val packageId = backStackEntry.arguments?.getString(Screen.Exam.ARG_PACKAGE_ID) ?: ""
            val context = LocalContext.current
            val app = context.applicationContext as BankSoalApplication

            val examViewModel: ExamViewModel = viewModel(
                key = "exam_${packageId}_${System.currentTimeMillis()}",
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return ExamViewModel(
                            examRepository = app.container.examRepository,
                            packageRepository = app.container.packageRepository
                        ) as T
                    }
                }
            )

            ExamScreen(
                packageId = packageId,
                viewModel = examViewModel,
                onFinishExam = { attemptId ->
                    navController.navigate(Screen.Result.createRoute(attemptId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument(Screen.Result.ARG_ATTEMPT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val attemptId = backStackEntry.arguments?.getString(Screen.Result.ARG_ATTEMPT_ID) ?: ""
            ResultScreen(
                attemptId = attemptId,
                onReviewClick = { attempt ->
                    navController.navigate(Screen.Review.createRoute(attempt))
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Review.route,
            arguments = listOf(
                navArgument(Screen.Review.ARG_ATTEMPT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val attemptId = backStackEntry.arguments?.getString(Screen.Review.ARG_ATTEMPT_ID) ?: ""
            ReviewScreen(
                attemptId = attemptId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Subscription.route) {
            SubscriptionScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}

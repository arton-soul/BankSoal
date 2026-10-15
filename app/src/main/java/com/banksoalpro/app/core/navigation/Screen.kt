package com.banksoalpro.app.core.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Beranda")

    object Catalog : Screen("catalog/{categoryId}", "Katalog Soal") {
        const val ARG_CATEGORY_ID = "categoryId"
        fun createRoute(categoryId: String) = "catalog/$categoryId"
    }

    object Exam : Screen("exam/{packageId}", "Simulasi Ujian") {
        const val ARG_PACKAGE_ID = "packageId"
        fun createRoute(packageId: String) = "exam/$packageId"
    }

    object Result : Screen("result/{attemptId}", "Hasil Ujian") {
        const val ARG_ATTEMPT_ID = "attemptId"
        fun createRoute(attemptId: String) = "result/$attemptId"
    }

    object Review : Screen("review/{attemptId}", "Pembahasan Soal") {
        const val ARG_ATTEMPT_ID = "attemptId"
        fun createRoute(attemptId: String) = "review/$attemptId"
    }

    object Subscription : Screen("subscription", "Akses Premium")

    object Profile : Screen("profile", "Akun & Profil")

    object Settings : Screen("settings", "Pengaturan")
}

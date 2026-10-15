package com.banksoalpro.app

import android.app.Application
import com.banksoalpro.app.core.database.SampleDataSeeder
import com.banksoalpro.app.core.di.AppContainer
import com.banksoalpro.app.core.di.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BankSoalApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        // Seed initial packages and questions if empty
        CoroutineScope(Dispatchers.IO).launch {
            val db = container.database
            val existing = db.examPackageDao().getPackageById("cpns_skd_free_01")
            if (existing == null) {
                db.examPackageDao().insertPackages(SampleDataSeeder.getInitialPackages())
                db.passageDao().insertPassages(SampleDataSeeder.getInitialPassages())
                db.questionDao().insertQuestions(SampleDataSeeder.getInitialQuestions())
            }
        }
    }
}

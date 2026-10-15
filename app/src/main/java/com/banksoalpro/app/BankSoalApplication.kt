package com.banksoalpro.app

import android.app.Application
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

        // Seed initial asset packages on background IO
        CoroutineScope(Dispatchers.IO).launch {
            container.syncManager.seedInitialAssetsIfEmpty()
        }
    }
}

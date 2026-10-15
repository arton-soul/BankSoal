package com.banksoalpro.app

import android.app.Application
import com.banksoalpro.app.core.database.BankSoalDatabase

class BankSoalApplication : Application() {

    val database: BankSoalDatabase by lazy {
        BankSoalDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}

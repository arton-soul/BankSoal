package com.banksoalpro.app.core.di

import android.content.Context
import com.banksoalpro.app.core.database.BankSoalDatabase
import com.banksoalpro.app.data.repository.ExamRepositoryImpl
import com.banksoalpro.app.data.repository.PackageRepositoryImpl
import com.banksoalpro.app.domain.repository.ExamRepository
import com.banksoalpro.app.domain.repository.PackageRepository

interface AppContainer {
    val database: BankSoalDatabase
    val packageRepository: PackageRepository
    val examRepository: ExamRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val database: BankSoalDatabase by lazy {
        BankSoalDatabase.getInstance(context)
    }

    override val packageRepository: PackageRepository by lazy {
        PackageRepositoryImpl(database.examPackageDao())
    }

    override val examRepository: ExamRepository by lazy {
        ExamRepositoryImpl(
            questionDao = database.questionDao(),
            passageDao = database.passageDao(),
            examAttemptDao = database.examAttemptDao()
        )
    }
}

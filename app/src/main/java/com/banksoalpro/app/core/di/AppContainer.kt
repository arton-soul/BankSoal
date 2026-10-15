package com.banksoalpro.app.core.di

import android.content.Context
import com.banksoalpro.app.core.database.BankSoalDatabase
import com.banksoalpro.app.data.remote.sync.ContentSyncManager
import com.banksoalpro.app.data.remote.sync.RemoteEntitlementGateway
import com.banksoalpro.app.data.remote.sync.RemoteEntitlementGatewayImpl
import com.banksoalpro.app.data.remote.sync.UserDataSyncManager
import com.banksoalpro.app.data.repository.AuthRepositoryImpl
import com.banksoalpro.app.data.repository.EntitlementRepositoryImpl
import com.banksoalpro.app.data.repository.ExamRepositoryImpl
import com.banksoalpro.app.data.repository.PackageRepositoryImpl
import com.banksoalpro.app.domain.repository.AuthRepository
import com.banksoalpro.app.domain.repository.EntitlementRepository
import com.banksoalpro.app.domain.repository.ExamRepository
import com.banksoalpro.app.domain.repository.PackageRepository

interface AppContainer {
    val database: BankSoalDatabase
    val packageRepository: PackageRepository
    val examRepository: ExamRepository
    val syncManager: ContentSyncManager
    val remoteEntitlementGateway: RemoteEntitlementGateway
    val authRepository: AuthRepository
    val entitlementRepository: EntitlementRepository
    val userDataSyncManager: UserDataSyncManager
    val billingRepository: com.banksoalpro.app.domain.repository.BillingRepository
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

    override val syncManager: ContentSyncManager by lazy {
        ContentSyncManager(database, context)
    }

    override val remoteEntitlementGateway: RemoteEntitlementGateway by lazy {
        RemoteEntitlementGatewayImpl()
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(database.userAccountDao())
    }

    override val entitlementRepository: EntitlementRepository by lazy {
        EntitlementRepositoryImpl(database.userAccountDao(), remoteEntitlementGateway)
    }

    override val userDataSyncManager: UserDataSyncManager by lazy {
        UserDataSyncManager(
            authRepository = authRepository,
            entitlementRepository = entitlementRepository,
            database = database,
            remoteGateway = remoteEntitlementGateway
        )
    }

    override val billingRepository: com.banksoalpro.app.domain.repository.BillingRepository by lazy {
        com.banksoalpro.app.data.repository.BillingRepositoryImpl(
            context = context,
            authRepository = authRepository,
            entitlementRepository = entitlementRepository
        )
    }
}

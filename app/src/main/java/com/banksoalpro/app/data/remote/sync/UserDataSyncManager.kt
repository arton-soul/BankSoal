package com.banksoalpro.app.data.remote.sync

import com.banksoalpro.app.core.database.BankSoalDatabase
import com.banksoalpro.app.data.repository.toDomain
import com.banksoalpro.app.domain.model.CloudSyncState
import com.banksoalpro.app.domain.repository.AuthRepository
import com.banksoalpro.app.domain.repository.EntitlementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserDataSyncManager(
    private val authRepository: AuthRepository,
    private val entitlementRepository: EntitlementRepository,
    private val database: BankSoalDatabase,
    private val remoteGateway: RemoteEntitlementGateway
) {
    private val _syncState = MutableStateFlow<CloudSyncState>(CloudSyncState.Idle)
    val syncState: StateFlow<CloudSyncState> = _syncState.asStateFlow()

    suspend fun syncAll(): CloudSyncState {
        _syncState.value = CloudSyncState.Syncing
        return try {
            val user = authRepository.getCurrentUser()
            if (user == null || user.isGuest) {
                val idleState = CloudSyncState.Idle
                _syncState.value = idleState
                return idleState
            }

            // 1. Sync entitlements
            val entitlementResult = entitlementRepository.syncWithCloud(user.userId)
            if (entitlementResult.isFailure) {
                val errorState = CloudSyncState.Error(
                    entitlementResult.exceptionOrNull()?.message ?: "Gagal sinkronisasi hak akses cloud"
                )
                _syncState.value = errorState
                return errorState
            }

            // 2. Sync attempts
            val localAttempts = database.examAttemptDao().getAllAttemptsSync().map { it.toDomain() }
            remoteGateway.syncAttempts(user.userId, localAttempts)

            val successState = CloudSyncState.Success(
                message = "Data berhasil disinkronkan ke Cloud",
                syncedAt = System.currentTimeMillis()
            )
            _syncState.value = successState
            successState
        } catch (e: Exception) {
            val errorState = CloudSyncState.Error(e.message ?: "Terjadi kesalahan sinkronisasi")
            _syncState.value = errorState
            errorState
        }
    }

    fun resetState() {
        _syncState.value = CloudSyncState.Idle
    }
}

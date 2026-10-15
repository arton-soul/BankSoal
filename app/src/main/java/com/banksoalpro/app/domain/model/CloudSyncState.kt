package com.banksoalpro.app.domain.model

sealed interface CloudSyncState {
    object Idle : CloudSyncState
    object Syncing : CloudSyncState
    data class Success(
        val message: String,
        val syncedAt: Long = System.currentTimeMillis()
    ) : CloudSyncState
    data class Error(
        val errorMessage: String
    ) : CloudSyncState
}

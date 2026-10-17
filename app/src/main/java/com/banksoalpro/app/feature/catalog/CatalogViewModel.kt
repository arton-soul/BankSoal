package com.banksoalpro.app.feature.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksoalpro.app.data.remote.sync.ContentSyncManager
import com.banksoalpro.app.domain.model.ExamPackage
import com.banksoalpro.app.domain.repository.PackageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SyncStatus {
    object Idle : SyncStatus
    object Syncing : SyncStatus
    data class Success(val message: String) : SyncStatus
    data class Error(val error: String) : SyncStatus
}

data class CatalogUiState(
    val categoryId: String = "",
    val packages: List<ExamPackage> = emptyList(),
    val isLoading: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus.Idle,
    val downloadingPackageId: String? = null,
    val entitlement: com.banksoalpro.app.domain.model.UserEntitlement = com.banksoalpro.app.domain.model.UserEntitlement(userId = "")
)

class CatalogViewModel(
    private val packageRepository: PackageRepository,
    private val syncManager: ContentSyncManager,
    private val entitlementRepository: com.banksoalpro.app.domain.repository.EntitlementRepository? = null,
    private val authRepository: com.banksoalpro.app.domain.repository.AuthRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        entitlementRepository?.let { repo ->
            viewModelScope.launch {
                repo.currentEntitlement.collect { ent ->
                    _uiState.update { it.copy(entitlement = ent) }
                }
            }
        }
    }

    fun unlockPackageWithReward(packageId: String) {
        viewModelScope.launch {
            val user = authRepository?.getCurrentUser()
            val userId = user?.userId ?: "guest_local"
            entitlementRepository?.unlockPackage(userId, packageId)
        }
    }

    fun loadCategory(categoryId: String) {
        _uiState.update { it.copy(categoryId = categoryId, isLoading = true) }
        viewModelScope.launch {
            packageRepository.getPackagesByCategory(categoryId).collect { list ->
                _uiState.update {
                    it.copy(packages = list, isLoading = false)
                }
            }
        }
    }

    fun syncFromGitHub() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncStatus = SyncStatus.Syncing) }
            val result = syncManager.syncCatalogFromGitHub()
            result.onSuccess { catalog ->
                _uiState.update {
                    it.copy(syncStatus = SyncStatus.Success("Berhasil memperbarui ${catalog.packages.size} paket soal!"))
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(syncStatus = SyncStatus.Error("Gagal memperbarui katalog: ${err.localizedMessage ?: "Koneksi offline"}"))
                }
            }
        }
    }

    fun downloadPackage(packageId: String, downloadUrl: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(downloadingPackageId = packageId) }
            val path = if (downloadUrl.isNotBlank()) downloadUrl else "packages/$packageId.json"
            val result = syncManager.downloadPackageFromGitHub(path)
            _uiState.update { it.copy(downloadingPackageId = null) }
            if (result.isFailure) {
                _uiState.update {
                    it.copy(syncStatus = SyncStatus.Error("Gagal mengunduh paket $packageId"))
                }
            }
        }
    }

    fun clearSyncStatus() {
        _uiState.update { it.copy(syncStatus = SyncStatus.Idle) }
    }
}

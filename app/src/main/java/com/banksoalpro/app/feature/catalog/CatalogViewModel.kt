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
    val downloadingPackageId: String? = null
)

class CatalogViewModel(
    private val packageRepository: PackageRepository,
    private val syncManager: ContentSyncManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

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
                    it.copy(syncStatus = SyncStatus.Success("Berhasil memperbarui ${catalog.packages.size} paket dari GitHub!"))
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(syncStatus = SyncStatus.Error("Gagal terhubung ke GitHub: ${err.localizedMessage ?: "Koneksi offline"}"))
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

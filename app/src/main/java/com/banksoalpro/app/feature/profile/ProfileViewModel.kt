package com.banksoalpro.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banksoalpro.app.core.database.BankSoalDatabase
import com.banksoalpro.app.data.remote.sync.UserDataSyncManager
import com.banksoalpro.app.domain.model.CloudSyncState
import com.banksoalpro.app.domain.model.UserEntitlement
import com.banksoalpro.app.domain.model.UserProfile
import com.banksoalpro.app.domain.repository.AuthRepository
import com.banksoalpro.app.domain.repository.EntitlementRepository
import com.banksoalpro.app.data.repository.toDomain
import com.banksoalpro.app.domain.model.ExamPackage
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UserExamStats(
    val totalCompletedTryouts: Int = 0,
    val averageScore: Double = 0.0,
    val totalBookmarks: Int = 0
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val entitlementRepository: EntitlementRepository,
    private val syncManager: UserDataSyncManager,
    private val database: BankSoalDatabase
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentEntitlement: StateFlow<UserEntitlement> = entitlementRepository.currentEntitlement
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserEntitlement(userId = ""))

    val syncState: StateFlow<CloudSyncState> = syncManager.syncState

    val allPackages: StateFlow<List<ExamPackage>> = database.examPackageDao().getAllPackages()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _stats = MutableStateFlow(UserExamStats())
    val stats: StateFlow<UserExamStats> = _stats.asStateFlow()

    init {
        // Ensure active user exists on launch
        viewModelScope.launch {
            authRepository.getCurrentUser()
            loadStats()
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            val attempts = database.examAttemptDao().getAllAttemptsSync()
            val completed = attempts.filter { it.isSubmitted }
            val avg = if (completed.isNotEmpty()) {
                completed.map { it.totalScore }.average()
            } else 0.0

            // Bookmarks count
            var bookmarkCount = 0
            try {
                val bookmarks = database.bookmarkDao().getBookmarkedQuestionIds()
                bookmarks.collect { list ->
                    _stats.value = UserExamStats(
                        totalCompletedTryouts = completed.size,
                        averageScore = avg,
                        totalBookmarks = list.size
                    )
                }
            } catch (_: Exception) {
                _stats.value = UserExamStats(
                    totalCompletedTryouts = completed.size,
                    averageScore = avg,
                    totalBookmarks = bookmarkCount
                )
            }
        }
    }

    fun signInWithGoogle(displayName: String, email: String, photoUrl: String? = null) {
        viewModelScope.launch {
            authRepository.signInWithGoogle(displayName, email, photoUrl)
            syncManager.syncAll()
            loadStats()
        }
    }

    fun signInAsGuest() {
        viewModelScope.launch {
            authRepository.signInAsGuest()
            loadStats()
        }
    }

    fun updateTargetExamCategory(category: String) {
        viewModelScope.launch {
            authRepository.updateTargetExamCategory(category)
        }
    }

    fun syncCloudData() {
        viewModelScope.launch {
            syncManager.syncAll()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            loadStats()
        }
    }
}

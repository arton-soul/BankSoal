package com.banksoalpro.app.domain.repository

import com.banksoalpro.app.domain.model.ExamPackage
import kotlinx.coroutines.flow.Flow

interface PackageRepository {
    fun getAllPackages(): Flow<List<ExamPackage>>
    fun getPackagesByCategory(categoryId: String): Flow<List<ExamPackage>>
    suspend fun getPackageById(packageId: String): ExamPackage?
    fun getPackageByIdFlow(packageId: String): Flow<ExamPackage?>
    suspend fun savePackages(packages: List<ExamPackage>)
    suspend fun updateDownloadStatus(packageId: String, isDownloaded: Boolean)
}

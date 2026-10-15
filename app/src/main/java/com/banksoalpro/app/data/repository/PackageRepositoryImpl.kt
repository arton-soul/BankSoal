package com.banksoalpro.app.data.repository

import com.banksoalpro.app.data.local.dao.ExamPackageDao
import com.banksoalpro.app.domain.model.ExamPackage
import com.banksoalpro.app.domain.repository.PackageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PackageRepositoryImpl(
    private val packageDao: ExamPackageDao
) : PackageRepository {

    override fun getAllPackages(): Flow<List<ExamPackage>> {
        return packageDao.getAllPackages().map { list -> list.map { it.toDomain() } }
    }

    override fun getPackagesByCategory(categoryId: String): Flow<List<ExamPackage>> {
        return packageDao.getPackagesByCategory(categoryId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getPackageById(packageId: String): ExamPackage? {
        return packageDao.getPackageById(packageId)?.toDomain()
    }

    override fun getPackageByIdFlow(packageId: String): Flow<ExamPackage?> {
        return packageDao.getPackageByIdFlow(packageId).map { it?.toDomain() }
    }

    override suspend fun savePackages(packages: List<ExamPackage>) {
        packageDao.insertPackages(packages.map { it.toEntity() })
    }

    override suspend fun updateDownloadStatus(packageId: String, isDownloaded: Boolean) {
        packageDao.updateDownloadStatus(packageId, isDownloaded)
    }
}

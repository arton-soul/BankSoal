package com.banksoalpro.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banksoalpro.app.data.local.entity.ExamPackageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamPackageDao {

    @Query("SELECT * FROM exam_packages ORDER BY categoryId, subCategory, id")
    fun getAllPackages(): Flow<List<ExamPackageEntity>>

    @Query("SELECT * FROM exam_packages WHERE categoryId = :categoryId ORDER BY id")
    fun getPackagesByCategory(categoryId: String): Flow<List<ExamPackageEntity>>

    @Query("SELECT * FROM exam_packages WHERE id = :packageId LIMIT 1")
    suspend fun getPackageById(packageId: String): ExamPackageEntity?

    @Query("SELECT * FROM exam_packages WHERE id = :packageId LIMIT 1")
    fun getPackageByIdFlow(packageId: String): Flow<ExamPackageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackages(packages: List<ExamPackageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(examPackage: ExamPackageEntity)

    @Update
    suspend fun updatePackage(examPackage: ExamPackageEntity)

    @Query("UPDATE exam_packages SET isDownloaded = :isDownloaded WHERE id = :packageId")
    suspend fun updateDownloadStatus(packageId: String, isDownloaded: Boolean)

    @Query("DELETE FROM exam_packages WHERE id = :packageId")
    suspend fun deletePackage(packageId: String)
}

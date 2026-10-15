package com.banksoalpro.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.banksoalpro.app.data.local.entity.ExamAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamAttemptDao {

    @Query("SELECT * FROM exam_attempts ORDER BY startedAt DESC")
    fun getAllAttempts(): Flow<List<ExamAttemptEntity>>

    @Query("SELECT * FROM exam_attempts ORDER BY startedAt DESC")
    suspend fun getAllAttemptsSync(): List<ExamAttemptEntity>

    @Query("SELECT * FROM exam_attempts WHERE packageId = :packageId ORDER BY startedAt DESC")
    fun getAttemptsByPackageId(packageId: String): Flow<List<ExamAttemptEntity>>

    @Query("SELECT * FROM exam_attempts WHERE attemptId = :attemptId LIMIT 1")
    suspend fun getAttemptById(attemptId: String): ExamAttemptEntity?

    @Query("SELECT * FROM exam_attempts WHERE attemptId = :attemptId LIMIT 1")
    fun getAttemptByIdFlow(attemptId: String): Flow<ExamAttemptEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: ExamAttemptEntity)

    @Update
    suspend fun updateAttempt(attempt: ExamAttemptEntity)

    @Query("DELETE FROM exam_attempts WHERE attemptId = :attemptId")
    suspend fun deleteAttempt(attemptId: String)
}

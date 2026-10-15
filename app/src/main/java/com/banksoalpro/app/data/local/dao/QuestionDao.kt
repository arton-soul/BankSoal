package com.banksoalpro.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.banksoalpro.app.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE packageId = :packageId ORDER BY questionNumber ASC")
    fun getQuestionsByPackageIdFlow(packageId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE packageId = :packageId ORDER BY questionNumber ASC")
    suspend fun getQuestionsByPackageId(packageId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :questionId LIMIT 1")
    suspend fun getQuestionById(questionId: String): QuestionEntity?

    @Query("SELECT COUNT(*) FROM questions WHERE packageId = :packageId")
    suspend fun getQuestionCountByPackageId(packageId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("DELETE FROM questions WHERE packageId = :packageId")
    suspend fun deleteQuestionsByPackageId(packageId: String)
}

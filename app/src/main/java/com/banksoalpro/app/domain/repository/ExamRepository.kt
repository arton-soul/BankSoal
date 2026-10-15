package com.banksoalpro.app.domain.repository

import com.banksoalpro.app.domain.model.ExamAttempt
import com.banksoalpro.app.domain.model.Passage
import com.banksoalpro.app.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    fun getQuestionsByPackageId(packageId: String): Flow<List<Question>>
    suspend fun getQuestionsListByPackageId(packageId: String): List<Question>
    suspend fun getPassageById(passageId: String): Passage?
    suspend fun saveQuestions(questions: List<Question>)
    suspend fun savePassages(passages: List<Passage>)

    fun getAttemptsByPackageId(packageId: String): Flow<List<ExamAttempt>>
    suspend fun getAttemptById(attemptId: String): ExamAttempt?
    suspend fun saveAttempt(attempt: ExamAttempt)
}

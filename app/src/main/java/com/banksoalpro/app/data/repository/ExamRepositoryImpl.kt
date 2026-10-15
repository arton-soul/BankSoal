package com.banksoalpro.app.data.repository

import com.banksoalpro.app.data.local.dao.ExamAttemptDao
import com.banksoalpro.app.data.local.dao.PassageDao
import com.banksoalpro.app.data.local.dao.QuestionDao
import com.banksoalpro.app.domain.model.ExamAttempt
import com.banksoalpro.app.domain.model.Passage
import com.banksoalpro.app.domain.model.Question
import com.banksoalpro.app.domain.repository.ExamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExamRepositoryImpl(
    private val questionDao: QuestionDao,
    private val passageDao: PassageDao,
    private val examAttemptDao: ExamAttemptDao
) : ExamRepository {

    override fun getQuestionsByPackageId(packageId: String): Flow<List<Question>> {
        return questionDao.getQuestionsByPackageIdFlow(packageId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getQuestionsListByPackageId(packageId: String): List<Question> {
        return questionDao.getQuestionsByPackageId(packageId).map { it.toDomain() }
    }

    override suspend fun getPassageById(passageId: String): Passage? {
        return passageDao.getPassageById(passageId)?.toDomain()
    }

    override suspend fun saveQuestions(questions: List<Question>) {
        questionDao.insertQuestions(questions.map { it.toEntity() })
    }

    override suspend fun savePassages(passages: List<Passage>) {
        passageDao.insertPassages(passages.map { it.toEntity() })
    }

    override fun getAttemptsByPackageId(packageId: String): Flow<List<ExamAttempt>> {
        return examAttemptDao.getAttemptsByPackageId(packageId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAttemptById(attemptId: String): ExamAttempt? {
        return examAttemptDao.getAttemptById(attemptId)?.toDomain()
    }

    override suspend fun saveAttempt(attempt: ExamAttempt) {
        examAttemptDao.insertAttempt(attempt.toEntity())
    }
}

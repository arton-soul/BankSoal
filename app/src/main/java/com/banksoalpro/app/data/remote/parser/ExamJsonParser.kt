package com.banksoalpro.app.data.remote.parser

import com.banksoalpro.app.data.local.entity.ExamPackageEntity
import com.banksoalpro.app.data.local.entity.PassageEntity
import com.banksoalpro.app.data.local.entity.QuestionEntity
import com.banksoalpro.app.data.remote.dto.*
import org.json.JSONArray
import org.json.JSONObject

object ExamJsonParser {

    fun parseCatalog(jsonString: String): CatalogJsonDto {
        val root = JSONObject(jsonString)
        val version = root.optInt("version", 1)
        val updatedAt = root.optString("updatedAt", "")
        val pkgsArray = root.optJSONArray("packages") ?: JSONArray()

        val packages = mutableListOf<PackageMetaDto>()
        for (i in 0 until pkgsArray.length()) {
            val obj = pkgsArray.getJSONObject(i)
            packages.add(parsePackageMeta(obj))
        }

        return CatalogJsonDto(version, updatedAt, packages)
    }

    fun parseFullPackage(jsonString: String): FullPackageJsonDto {
        val root = JSONObject(jsonString)
        val packageObj = root.optJSONObject("package") 
            ?: root.optJSONObject("packageMeta") 
            ?: root.getJSONObject("package")
        val metaDto = parsePackageMeta(packageObj)

        val passages = mutableListOf<PassageJsonDto>()
        val passagesArray = root.optJSONArray("passages")
        if (passagesArray != null) {
            for (i in 0 until passagesArray.length()) {
                val pObj = passagesArray.getJSONObject(i)
                passages.add(
                    PassageJsonDto(
                        id = pObj.getString("id"),
                        title = pObj.optString("title").takeIf { it.isNotBlank() },
                        content = pObj.getString("content")
                    )
                )
            }
        }

        val questions = mutableListOf<QuestionJsonDto>()
        val questionsArray = root.getJSONArray("questions")
        for (i in 0 until questionsArray.length()) {
            val qObj = questionsArray.getJSONObject(i)
            questions.add(
                QuestionJsonDto(
                    id = qObj.getString("id"),
                    packageId = qObj.optString("packageId", metaDto.id),
                    passageId = qObj.optString("passageId").takeIf { it.isNotBlank() && it != "null" },
                    sectionName = qObj.optString("sectionName", "Umum"),
                    questionNumber = qObj.optInt("questionNumber", i + 1),
                    questionText = qObj.getString("questionText"),
                    imageUrl = qObj.optString("imageUrl").takeIf { it.isNotBlank() && it != "null" },
                    optionA = qObj.getString("optionA"),
                    optionB = qObj.getString("optionB"),
                    optionC = qObj.getString("optionC"),
                    optionD = qObj.getString("optionD"),
                    optionE = qObj.optString("optionE").takeIf { it.isNotBlank() && it != "null" },
                    scoringType = qObj.optString("scoringType", "STANDARD"),
                    correctAnswer = qObj.getString("correctAnswer"),
                    scoreA = qObj.optInt("scoreA", 0),
                    scoreB = qObj.optInt("scoreB", 0),
                    scoreC = qObj.optInt("scoreC", 0),
                    scoreD = qObj.optInt("scoreD", 0),
                    scoreE = qObj.optInt("scoreE", 0),
                    explanation = qObj.optString("explanation", ""),
                    explanationImageUrl = qObj.optString("explanationImageUrl").takeIf { it.isNotBlank() && it != "null" }
                )
            )
        }

        return FullPackageJsonDto(metaDto, passages, questions)
    }

    private fun parsePackageMeta(obj: JSONObject): PackageMetaDto {
        return PackageMetaDto(
            id = obj.getString("id"),
            categoryId = obj.getString("categoryId"),
            subCategory = obj.optString("subCategory", ""),
            title = obj.getString("title"),
            description = obj.optString("description", ""),
            totalQuestions = obj.optInt("totalQuestions", 0),
            durationMinutes = obj.optInt("durationMinutes", 60),
            passingScore = obj.optInt("passingScore", 0),
            isFree = obj.optBoolean("isFree", false),
            priceIdr = obj.optLong("priceIdr", 0L),
            version = obj.optInt("version", 1),
            downloadUrl = obj.optString("downloadUrl", "")
        )
    }

    fun toEntities(dto: FullPackageJsonDto): Triple<ExamPackageEntity, List<PassageEntity>, List<QuestionEntity>> {
        val packageEntity = ExamPackageEntity(
            id = dto.packageMeta.id,
            categoryId = dto.packageMeta.categoryId,
            subCategory = dto.packageMeta.subCategory,
            title = dto.packageMeta.title,
            description = dto.packageMeta.description,
            totalQuestions = if (dto.packageMeta.totalQuestions > 0) dto.packageMeta.totalQuestions else dto.questions.size,
            durationMinutes = dto.packageMeta.durationMinutes,
            passingScore = dto.packageMeta.passingScore,
            isFree = dto.packageMeta.isFree,
            priceIdr = dto.packageMeta.priceIdr,
            isDownloaded = true,
            version = dto.packageMeta.version
        )

        val passageEntities = dto.passages.map { p ->
            PassageEntity(
                id = p.id,
                title = p.title,
                content = p.content
            )
        }

        val questionEntities = dto.questions.map { q ->
            QuestionEntity(
                id = q.id,
                packageId = packageEntity.id,
                passageId = q.passageId,
                sectionName = q.sectionName,
                questionNumber = q.questionNumber,
                questionText = q.questionText,
                imageUrl = q.imageUrl,
                optionA = q.optionA,
                optionB = q.optionB,
                optionC = q.optionC,
                optionD = q.optionD,
                optionE = q.optionE,
                scoringType = q.scoringType,
                correctAnswer = q.correctAnswer,
                scoreA = q.scoreA,
                scoreB = q.scoreB,
                scoreC = q.scoreC,
                scoreD = q.scoreD,
                scoreE = q.scoreE,
                explanation = q.explanation,
                explanationImageUrl = q.explanationImageUrl
            )
        }

        return Triple(packageEntity, passageEntities, questionEntities)
    }
}

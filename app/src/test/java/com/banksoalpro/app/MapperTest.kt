package com.banksoalpro.app

import com.banksoalpro.app.data.local.entity.ExamPackageEntity
import com.banksoalpro.app.data.repository.toDomain
import com.banksoalpro.app.data.repository.toEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperTest {

    @Test
    fun examPackageEntity_mapsToDomainAndBack() {
        val entity = ExamPackageEntity(
            id = "cpns_01",
            categoryId = "CPNS",
            subCategory = "SKD",
            title = "Tryout Akbar 1",
            description = "Simulasi SKD",
            totalQuestions = 110,
            durationMinutes = 100,
            passingScore = 311,
            isFree = true,
            priceIdr = 0,
            isDownloaded = true,
            version = 1
        )

        val domain = entity.toDomain()
        assertEquals(entity.id, domain.id)
        assertEquals(entity.title, domain.title)

        val backToEntity = domain.toEntity()
        assertEquals(entity, backToEntity)
    }
}

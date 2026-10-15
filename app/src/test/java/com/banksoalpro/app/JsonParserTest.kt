package com.banksoalpro.app

import com.banksoalpro.app.data.remote.parser.ExamJsonParser
import org.junit.Assert.*
import org.junit.Test

class JsonParserTest {

    private val sampleCatalogJson = """
        {
          "version": 2,
          "updatedAt": "2026-10-15T08:00:00Z",
          "packages": [
            {
              "id": "cpns_01",
              "categoryId": "CPNS",
              "subCategory": "SKD",
              "title": "Tryout Akbar 1",
              "description": "Simulasi SKD CPNS",
              "totalQuestions": 110,
              "durationMinutes": 100,
              "passingScore": 311,
              "isFree": true,
              "priceIdr": 0,
              "version": 2,
              "downloadUrl": "packages/cpns_01.json"
            }
          ]
        }
    """.trimIndent()

    private val samplePackageJson = """
        {
          "package": {
            "id": "cpns_01",
            "categoryId": "CPNS",
            "subCategory": "SKD",
            "title": "Tryout Akbar 1",
            "description": "Simulasi SKD CPNS",
            "totalQuestions": 1,
            "durationMinutes": 100,
            "passingScore": 311,
            "isFree": true,
            "priceIdr": 0,
            "version": 2,
            "downloadUrl": "packages/cpns_01.json"
          },
          "passages": [
            {
              "id": "pass_01",
              "title": "Wacana Nasional",
              "content": "Teks bacaan panjang..."
            }
          ],
          "questions": [
            {
              "id": "q_01",
              "packageId": "cpns_01",
              "passageId": "pass_01",
              "sectionName": "TWK",
              "questionNumber": 1,
              "questionText": "Pancasila lahir pada tanggal?",
              "imageUrl": null,
              "optionA": "1 Juni 1945",
              "optionB": "17 Agustus 1945",
              "optionC": "18 Agustus 1945",
              "optionD": "28 Oktober 1928",
              "optionE": "20 Mei 1908",
              "scoringType": "STANDARD",
              "correctAnswer": "A",
              "scoreA": 5,
              "scoreB": 0,
              "scoreC": 0,
              "scoreD": 0,
              "scoreE": 0,
              "explanation": "Pidato Bung Karno 1 Juni 1945."
            }
          ]
        }
    """.trimIndent()

    @Test
    fun testParseCatalog_success() {
        val catalog = ExamJsonParser.parseCatalog(sampleCatalogJson)
        assertEquals(2, catalog.version)
        assertEquals(1, catalog.packages.size)

        val pkg = catalog.packages.first()
        assertEquals("cpns_01", pkg.id)
        assertEquals("CPNS", pkg.categoryId)
        assertTrue(pkg.isFree)
    }

    @Test
    fun testParseFullPackageAndEntities_success() {
        val fullPackage = ExamJsonParser.parseFullPackage(samplePackageJson)
        assertEquals("cpns_01", fullPackage.packageMeta.id)
        assertEquals(1, fullPackage.passages.size)
        assertEquals(1, fullPackage.questions.size)

        val (pkgEntity, passageEntities, questionEntities) = ExamJsonParser.toEntities(fullPackage)
        assertEquals("cpns_01", pkgEntity.id)
        assertTrue(pkgEntity.isDownloaded)
        assertEquals(1, passageEntities.size)
        assertEquals("pass_01", passageEntities.first().id)
        assertEquals(1, questionEntities.size)
        assertEquals("1 Juni 1945", questionEntities.first().optionA)
        assertEquals("A", questionEntities.first().correctAnswer)
    }
}

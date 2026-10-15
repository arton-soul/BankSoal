package com.banksoalpro.app.data.remote.sync

import android.content.Context
import com.banksoalpro.app.core.common.AppConstants
import com.banksoalpro.app.core.database.BankSoalDatabase
import com.banksoalpro.app.data.local.entity.ExamPackageEntity
import com.banksoalpro.app.data.remote.dto.CatalogJsonDto
import com.banksoalpro.app.data.remote.parser.ExamJsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ContentSyncManager(
    private val database: BankSoalDatabase,
    private val context: Context
) {

    /**
     * Memuat paket awal dari folder assets/seed saat aplikasi pertama kali dijalankan
     */
    suspend fun seedInitialAssetsIfEmpty() = withContext(Dispatchers.IO) {
        val packageDao = database.examPackageDao()
        val passageDao = database.passageDao()
        val questionDao = database.questionDao()

        val existing = packageDao.getPackageById("cpns_skd_free_01")
        if (existing == null) {
            try {
                val assetManager = context.assets
                val files = assetManager.list("seed/packages") ?: emptyArray()

                for (filename in files) {
                    if (filename.endsWith(".json")) {
                        val jsonString = assetManager.open("seed/packages/$filename").bufferedReader().use { it.readText() }
                        val fullPackageDto = ExamJsonParser.parseFullPackage(jsonString)
                        val (pkg, passages, questions) = ExamJsonParser.toEntities(fullPackageDto)

                        packageDao.insertPackage(pkg.copy(isDownloaded = true))
                        if (passages.isNotEmpty()) passageDao.insertPassages(passages)
                        if (questions.isNotEmpty()) questionDao.insertQuestions(questions)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Mengambil indeks katalog terbaru langsung dari GitHub raw repository
     */
    suspend fun syncCatalogFromGitHub(): Result<CatalogJsonDto> = withContext(Dispatchers.IO) {
        try {
            val urlString = "${AppConstants.GITHUB_CONTENT_RAW_BASE}catalog.json"
            val jsonString = httpGet(urlString)
            val catalogDto = ExamJsonParser.parseCatalog(jsonString)

            val packageDao = database.examPackageDao()
            // Perbarui metadata paket di database lokal
            catalogDto.packages.forEach { meta ->
                val local = packageDao.getPackageById(meta.id)
                val isDownloaded = local?.isDownloaded ?: false
                val entity = ExamPackageEntity(
                    id = meta.id,
                    categoryId = meta.categoryId,
                    subCategory = meta.subCategory,
                    title = meta.title,
                    description = meta.description,
                    totalQuestions = meta.totalQuestions,
                    durationMinutes = meta.durationMinutes,
                    passingScore = meta.passingScore,
                    isFree = meta.isFree,
                    priceIdr = meta.priceIdr,
                    isDownloaded = isDownloaded,
                    version = meta.version
                )
                packageDao.insertPackage(entity)
            }

            Result.success(catalogDto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mengunduh paket soal lengkap (wacana + ribuan soal) dari GitHub ke Room DB lokal
     */
    suspend fun downloadPackageFromGitHub(downloadPath: String): Result<ExamPackageEntity> = withContext(Dispatchers.IO) {
        try {
            val cleanPath = downloadPath.removePrefix("/")
            val urlString = "${AppConstants.GITHUB_CONTENT_RAW_BASE}$cleanPath"
            val jsonString = httpGet(urlString)

            val fullPackageDto = ExamJsonParser.parseFullPackage(jsonString)
            val (pkg, passages, questions) = ExamJsonParser.toEntities(fullPackageDto)

            val packageDao = database.examPackageDao()
            val passageDao = database.passageDao()
            val questionDao = database.questionDao()

            packageDao.insertPackage(pkg.copy(isDownloaded = true))
            if (passages.isNotEmpty()) passageDao.insertPassages(passages)
            if (questions.isNotEmpty()) questionDao.insertQuestions(questions)

            Result.success(pkg.copy(isDownloaded = true))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun httpGet(urlString: String): String {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 10000
        conn.readTimeout = 15000
        conn.instanceFollowRedirects = true

        val responseCode = conn.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw java.io.IOException("Gagal mengambil data dari GitHub: HTTP $responseCode")
        }

        return conn.inputStream.bufferedReader().use(BufferedReader::readText)
    }
}

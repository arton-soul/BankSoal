package com.banksoalpro.app.core.database

import com.banksoalpro.app.data.local.entity.ExamPackageEntity
import com.banksoalpro.app.data.local.entity.PassageEntity
import com.banksoalpro.app.data.local.entity.QuestionEntity

object SampleDataSeeder {

    fun getInitialPackages(): List<ExamPackageEntity> = listOf(
        ExamPackageEntity(
            id = "cpns_skd_free_01",
            categoryId = "CPNS",
            subCategory = "SKD CPNS",
            title = "Tryout Akbar SKD CPNS 2026 (Starter)",
            description = "Simulasi lengkap 110 butir soal: 30 TWK, 35 TIU, 45 TKP sesuai kisi-kisi MenPAN-RB terbaru.",
            totalQuestions = 10, // Starter package sample
            durationMinutes = 15,
            passingScore = 311,
            isFree = true,
            isDownloaded = true,
            version = 1
        ),
        ExamPackageEntity(
            id = "utbk_snbt_free_01",
            categoryId = "UTBK",
            subCategory = "TPS & Literasi",
            title = "Simulasi UTBK-SNBT 2026 Gratis",
            description = "Kemampuan Penalaran Umum, Pengetahuan Kuantitatif, Pemahaman Bacaan, dan Literasi.",
            totalQuestions = 5,
            durationMinutes = 10,
            passingScore = 650,
            isFree = true,
            isDownloaded = true,
            version = 1
        ),
        ExamPackageEntity(
            id = "sma_sample_01",
            categoryId = "SMA",
            subCategory = "Asesmen Sumatif",
            title = "Tryout Standar SMA 2026",
            description = "Latihan pemantapan materi dan evaluasi kompetensi kurikulum merdeka.",
            totalQuestions = 5,
            durationMinutes = 10,
            passingScore = 75,
            isFree = true,
            isDownloaded = true,
            version = 1
        )
    )

    fun getInitialPassages(): List<PassageEntity> = listOf(
        PassageEntity(
            id = "passage_literasi_01",
            title = "Peran Kecerdasan Buatan dalam Transformasi Pendidikan Nasional",
            content = """
                Perkembangan pesat kecerdasan buatan (Artificial Intelligence/AI) telah membawa transformasi mendasar dalam berbagai sendi kehidupan, terutama di sektor pendidikan. Di era digital saat ini, platform pembelajaran adaptif berbasis AI mampu memetakan kekuatan serta kelemahan peserta didik secara real-time, memberikan rekomendasi bahan belajar yang terpersonalisasi, dan memfasilitasi evaluasi formatif yang berkesinambungan.

                Kendati demikian, integrasi AI dalam dunia pendidikan menghadirkan tantangan etis dan pedagogis yang tidak boleh diabaikan. Salah satu kekhawatiran terbesar adalah menurunnya kemampuan berpikir kritis dan orisinalitas generasi muda akibat ketergantungan yang berlebihan pada sistem otomatis. Selain itu, kesenjangan akses teknologi antarwilayah di Indonesia masih menjadi pekerjaan rumah besar. Pemanfaatan AI harus diposisikan sebagai akselerator peran guru, bukan pengganti esensi keteladanan dan interaksi emosional antara pendidik dan murid.
            """.trimIndent()
        )
    )

    fun getInitialQuestions(): List<QuestionEntity> = listOf(
        // === CPNS SKD Starter ===
        QuestionEntity(
            id = "cpns_twk_01",
            packageId = "cpns_skd_free_01",
            sectionName = "TWK (Nasionalisme)",
            questionNumber = 1,
            questionText = "Sikap mengutamakan kepentingan bangsa dan negara di atas kepentingan pribadi atau golongan yang diwujudkan dalam pengabdian tulus merupakan implementasi dari nilai Pancasila sila ke...",
            optionA = "Sila Pertama",
            optionB = "Sila Kedua",
            optionC = "Sila Ketiga",
            optionD = "Sila Keempat",
            optionE = "Sila Kelima",
            scoringType = "STANDARD",
            correctAnswer = "C",
            scoreA = 0, scoreB = 0, scoreC = 5, scoreD = 0, scoreE = 0,
            explanation = "Sila Ketiga 'Persatuan Indonesia' memuat butir pengamalan menempatkan persatuan, kesatuan, kepentingan serta keselamatan bangsa dan negara di atas kepentingan pribadi atau golongan."
        ),
        QuestionEntity(
            id = "cpns_twk_02",
            packageId = "cpns_skd_free_01",
            sectionName = "TWK (Integritas)",
            questionNumber = 2,
            questionText = "Seorang ASN menolak pemberian bingkisan berharga dari rekanan proyek pemerintah setelah proses tender selesai. Perilaku ASN tersebut mencerminkan prinsip integritas anti...",
            optionA = "Gratifikasi",
            optionB = "Suap aktif",
            optionC = "Pemerasan jabatan",
            optionD = "Benturan kepentingan",
            optionE = "Nepotisme",
            scoringType = "STANDARD",
            correctAnswer = "A",
            scoreA = 5, scoreB = 0, scoreC = 0, scoreD = 0, scoreE = 0,
            explanation = "Pemberian dalam arti luas berupa uang, barang, diskon, atau fasilitas lain kepada pegawai negeri atau penyelenggara negara yang berhubungan dengan jabatannya dikategorikan sebagai gratifikasi (Pasal 12B UU Tipikor)."
        ),
        QuestionEntity(
            id = "cpns_tiu_01",
            packageId = "cpns_skd_free_01",
            sectionName = "TIU (Analogi)",
            questionNumber = 3,
            questionText = "KERTAS : PENA = ... : ...",
            optionA = "Dinding : Kuas",
            optionB = "Meja : Kursi",
            optionC = "Papan tulis : Spidol",
            optionD = "Mobil : Bensin",
            optionE = "Buku : Perpustakaan",
            scoringType = "STANDARD",
            correctAnswer = "C",
            scoreA = 0, scoreB = 0, scoreC = 5, scoreD = 0, scoreE = 0,
            explanation = "Hubungan analogi: Pena digunakan sebagai alat untuk menulis di atas media Kertas. Sama halnya dengan Spidol yang digunakan sebagai alat untuk menulis di atas media Papan tulis."
        ),
        QuestionEntity(
            id = "cpns_tiu_02",
            packageId = "cpns_skd_free_01",
            sectionName = "TIU (Deret Angka)",
            questionNumber = 4,
            questionText = "Berapakah angka berikutnya dari deret: 3, 6, 12, 24, 48, ... ?",
            optionA = "72",
            optionB = "84",
            optionC = "96",
            optionD = "108",
            optionE = "120",
            scoringType = "STANDARD",
            correctAnswer = "C",
            scoreA = 0, scoreB = 0, scoreC = 5, scoreD = 0, scoreE = 0,
            explanation = "Pola deret adalah perkalian dengan 2 secara konstan: 3 x 2 = 6, 6 x 2 = 12, 12 x 2 = 24, 24 x 2 = 48, 48 x 2 = 96."
        ),
        QuestionEntity(
            id = "cpns_tiu_03",
            packageId = "cpns_skd_free_01",
            sectionName = "TIU (Silogisme)",
            questionNumber = 5,
            questionText = "Semua atlet profesional berdisiplin tinggi dalam menjaga kebugaran tubuh. Sebagian mahasiswa di kampus A adalah atlet profesional. Kesimpulan yang sah adalah...",
            optionA = "Semua mahasiswa di kampus A berdisiplin tinggi menjaga kebugaran tubuh.",
            optionB = "Sebagian mahasiswa di kampus A berdisiplin tinggi dalam menjaga kebugaran tubuh.",
            optionC = "Semua yang berdisiplin tinggi menjaga kebugaran tubuh adalah mahasiswa di kampus A.",
            optionD = "Tidak ada atlet profesional yang merupakan mahasiswa di kampus A.",
            optionE = "Mahasiswa di kampus A yang bukan atlet tidak menjaga kebugaran tubuh.",
            scoringType = "STANDARD",
            correctAnswer = "B",
            scoreA = 0, scoreB = 5, scoreC = 0, scoreD = 0, scoreE = 0,
            explanation = "Premis mayor (Semua) + Premis minor (Sebagian) menghasilkan kesimpulan bersifat partikular (Sebagian). Maka kesimpulan yang tepat: Sebagian mahasiswa di kampus A berdisiplin tinggi menjaga kebugaran tubuh."
        ),
        QuestionEntity(
            id = "cpns_tkp_01",
            packageId = "cpns_skd_free_01",
            sectionName = "TKP (Pelayanan Publik)",
            questionNumber = 6,
            questionText = "Saat jam istirahat kantor baru saja dimulai, datang seorang warga lansia yang menempuh perjalanan jauh hendak mengurus dokumen kependudukan mendesak. Sikap Anda adalah...",
            optionA = "Menjelaskan dengan ramah bahwa jam pelayanan sedang istirahat dan memintanya menunggu 1 jam.",
            optionB = "Melayani lansia tersebut terlebih dahulu hingga selesai sebelum menikmati waktu istirahat saya.",
            optionC = "Menyarankan warga tersebut untuk menitipkan berkas kepada satpam kantor.",
            optionD = "Memanggil rekan kerja yang sedang luang untuk menggantikan melayani lansia tersebut.",
            optionE = "Menolak permohonan dengan tegas sesuai SOP demi kedisiplinan jam kerja instansi.",
            scoringType = "TKP_WEIGHTED",
            correctAnswer = "B",
            scoreA = 3, scoreB = 5, scoreC = 2, scoreD = 4, scoreE = 1,
            explanation = "Bobot TKP: Opsi B bernilai 5 karena mengedepankan empati dan inisiatif pelayanan publik prima untuk situasi darurat dan rentan (lansia)."
        ),
        QuestionEntity(
            id = "cpns_tkp_02",
            packageId = "cpns_skd_free_01",
            sectionName = "TKP (Teknologi Informasi)",
            questionNumber = 7,
            questionText = "Instansi tempat Anda bekerja akan memberlakukan sistem digitalisasi arsip baru yang memerlukan adaptasi keterampilan teknologi baru. Respon Anda adalah...",
            optionA = "Mengikuti pelatihan dengan sungguh-sungguh dan aktif mempelajari fitur-fitur sistem tersebut.",
            optionB = "Menunggu arahan dan contoh dari rekan kerja sebelum mulai menggunakan sistem.",
            optionC = "Tetap memakai cara manual selama sistem baru belum sepenuhnya stabil.",
            optionD = "Mempelajari sistem secara mandiri serta siap membantu rekan yang mengalami kesulitan.",
            optionE = "Mengusulkan agar sistem lama tetap dipertahankan karena sudah terbukti berjalan bertahun-tahun.",
            scoringType = "TKP_WEIGHTED",
            correctAnswer = "D",
            scoreA = 4, scoreB = 2, scoreC = 1, scoreD = 5, scoreE = 1,
            explanation = "Bobot TKP: Opsi D bernilai 5 karena memperlihatkan sikap proaktif, adaptif terhadap perkembangan teknologi, dan kolaboratif bagi tim."
        ),

        // === UTBK SNBT Starter (With Reading Passage) ===
        QuestionEntity(
            id = "utbk_lit_01",
            packageId = "utbk_snbt_free_01",
            passageId = "passage_literasi_01",
            sectionName = "Literasi Bahasa Indonesia",
            questionNumber = 1,
            questionText = "Berdasarkan wacana di atas, gagasan pokok paragraf pertama adalah...",
            optionA = "Dampak negatif kecerdasan buatan terhadap generasi muda.",
            optionB = "Peran kecerdasan buatan dalam mentransformasi sektor pendidikan secara adaptif.",
            optionC = "Tantangan etika yang dihadapi oleh pendidik di era digital.",
            optionD = "Kesenjangan sarana dan infrastruktur teknologi di Indonesia.",
            optionE = "Perlunya menggantikan peran guru dengan sistem pembelajaran otomatis.",
            scoringType = "STANDARD",
            correctAnswer = "B",
            scoreA = 0, scoreB = 1, scoreC = 0, scoreD = 0, scoreE = 0,
            explanation = "Paragraf pertama berfokus pada bagaimana perkembangan AI membawa perubahan mendasar di sektor pendidikan melalui pemetaan kelemahan dan pembelajaran adaptif."
        ),
        QuestionEntity(
            id = "utbk_lit_02",
            packageId = "utbk_snbt_free_01",
            passageId = "passage_literasi_01",
            sectionName = "Literasi Bahasa Indonesia",
            questionNumber = 2,
            questionText = "Menurut teks bacaan, mengapa pemanfaatan AI tidak boleh menggantikan peran seorang guru?",
            optionA = "Karena biaya implementasi AI terlalu mahal untuk sekolah-sekolah di pelosok.",
            optionB = "Karena AI belum mampu menganalisis hasil belajar peserta didik secara akurat.",
            optionC = "Karena esensi keteladanan dan interaksi emosional pendidik tidak dapat digantikan oleh teknologi.",
            optionD = "Karena sistem otomatis sering mengalami kesalahan komputasi sistem.",
            optionE = "Karena siswa lebih menyukai metode pembelajaran ceramah konvensional.",
            scoringType = "STANDARD",
            correctAnswer = "C",
            scoreA = 0, scoreB = 0, scoreC = 1, scoreD = 0, scoreE = 0,
            explanation = "Kalimat terakhir pada paragraf kedua menyatakan secara gamblang bahwa AI adalah akselerator dan bukan pengganti esensi keteladanan dan interaksi emosional antara pendidik dan murid."
        ),
        QuestionEntity(
            id = "utbk_pen_01",
            packageId = "utbk_snbt_free_01",
            sectionName = "Penalaran Matematika",
            questionNumber = 3,
            questionText = "Sebuah toko buku memberikan diskon bertingkat 20% kemudian tambahan 10% untuk buku tryout seharga Rp 100.000. Berapakah harga akhir yang harus dibayar pembeli?",
            optionA = "Rp 70.000",
            optionB = "Rp 72.000",
            optionC = "Rp 74.000",
            optionD = "Rp 75.000",
            optionE = "Rp 78.000",
            scoringType = "STANDARD",
            correctAnswer = "B",
            scoreA = 0, scoreB = 1, scoreC = 0, scoreD = 0, scoreE = 0,
            explanation = "Diskon pertama 20%: Rp 100.000 - Rp 20.000 = Rp 80.000. Diskon kedua 10% dari Rp 80.000: Rp 8.000. Maka harga akhir = Rp 80.000 - Rp 8.000 = Rp 72.000."
        )
    )
}

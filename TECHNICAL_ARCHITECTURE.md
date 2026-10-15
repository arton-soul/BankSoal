# Arsitektur & Pola Teknis: Bank Soal Pro
**Fondasi Teknis Berbasis EduQuest (Hybrid / Cherry-Pick Approach)**

Dokumen ini merinci rancangan arsitektur dan pola teknis untuk pengembangan aplikasi **Bank Soal Pro** (SMP, SMA, UTBK/SNBT, CPNS/PPPK, dan Ujian Kedinasan). Dokumen ini mengambil keunggulan arsitektural dari proyek *EduQuest* sekaligus mengeliminasi fitur-fitur yang tidak relevan dan menambahkan pilar-pilar penting untuk kebutuhan ujian formal serta monetisasi.

---

## 1. Filosofi & Pendekatan Hybrid (Cherry-Pick)

Pendekatan *Cherry-Pick* mengadopsi pola teknis terbaik yang sudah teruji pada *EduQuest* (Clean Architecture, Jetpack Compose, Room Offline-First, Unidirectional Data Flow), namun membuang komponen gamifikasi anak-anak dan menggantinya dengan fitur simulasi ujian modern (CAT), render rumus/wacana, serta sistem monetisasi.

```
EduQuest (Basis Referensi)               Bank Soal Pro (Target Baru)
┌─────────────────────────────┐         ┌─────────────────────────────┐
│ ✅ Kotlin 2.0 + Compose M3  │ ──────> │ ✅ Kotlin 2.0 + Compose M3  │
│ ✅ Room DB & Offline-First  │ ──────> │ ✅ Room DB + Cloud Cache    │
│ ✅ MVVM + Clean Architecture│ ──────> │ ✅ MVVM + Clean Architecture│
│ ✅ Timer & Quiz State Engine│ ──Adapt─> │ ✅ Engine CAT (Grid, Ragu²) │
│                             │         │                             │
│ ❌ Quest, RPG, Avatar, Map  │ (Dibuang│ ⭐ Pilihan 5 Opsi (A - E)   │
│ ❌ Kartu Edukasi & SFX Lucu │)        │ ⭐ Soal Bobot TKP (1-5 Poin)│
│ ❌ Soal Hardcoded di APK    │         │ ⭐ Wacana/Bacaan & Rumus    │
│ ❌ Tanpa Monetisasi/Billing │         │ ⭐ AdMob + Play Billing     │
└─────────────────────────────┘         └─────────────────────────────┘
```

---

## 2. Pemetaan Komponen (Cherry-Pick Mapping)

### A. Komponen yang Diambil Langsung (Reused As-Is)
1. **Tech Stack & Gradle Foundation:**
   - Kotlin 2.0 + Android Gradle Plugin 8.5+.
   - Jetpack Compose + Material 3 + Navigation Compose.
   - Coroutines + Kotlin Flow untuk reaktivitas data.
2. **Arsitektur Tiga Lapis (Clean Architecture):**
   - `data`: Room Database, DAO, Entity, Repository Implementation, Data Sources.
   - `domain`: Model Bisnis murni, Repository Interface, Use Cases.
   - `feature`/`presentation`: Jetpack Compose Screens, ViewModels, UI State & Event.
3. **Pola State Management UI:**
   - Pemisahan `UiState` (Immutable data class), `UiEvent` (One-off events seperti navigasi/snack bar), dan `ViewModel` (Single source of truth).

### B. Komponen EduQuest yang Dibuang (Discarded)
- ❌ Sistem Avatar, Level RPG, Sound FX kartun, World/Area Map RPG.
- ❌ Knowledge Card, Daily Mission bertema game anak.
- ❌ Hardcoded 100 soal SD statis di dalam file kode Kotlin.

### C. Komponen Baru & Adaptasi Khusus (New & Adapted)
- ⭐ **Skema Soal Fleksibel:** Mendukung 5 pilihan ($A, B, C, D, E$), tipe soal standar, soal minus (UTBK lama/tertentu), serta skor bobot 1–5 untuk Tes Karakteristik Pribadi (TKP CPNS).
- ⭐ **Teks Wacana & Gambar:** Dukungan soal dengan teks bacaan panjang (*reading passage*) yang digunakan bersama oleh beberapa nomor soal.
- ⭐ **Engine Simulasi CAT (Computer Assisted Test):**
  - Timer hitung mundur per subtes/keseluruhan.
  - Dialog konfirmasi lembar jawaban.
  - Grid daftar nomor soal dengan indikator status: *Terjawab*, *Ragu-ragu*, *Belum Terjawab*.
- ⭐ **Hybrid Content Delivery (Cloud to Local Room):**
  - Metadata paket soal diambil dari Cloud (Firebase Firestore / Supabase / REST JSON API).
  - Soal di-download per paket ke database Room lokal pengguna agar bisa dikerjakan secara offline.
- ⭐ **Lapisan Monetisasi Terintegrasi:**
  - Google Mobile Ads (AdMob): Banner, Interstitial, Rewarded Ads.
  - Google Play Billing Library: In-App Purchase paket soal & langganan Akun Premium.
  - Firebase Authentication (Google Sign-In) untuk sinkronisasi hak akses pembelian.

---

## 3. Struktur Direktori Proyek (Package Structure)

```text
com.banksoalpro.app
│
├── core
│   ├── common               // Result wrapper, Dispatcher provider, Konstanta
│   ├── database             // Room Database, TypeConverters, Base DAO
│   ├── network              // Retrofit / Ktor / Supabase client, DTOs
│   ├── navigation           // NavGraph, Screen routes, Navigation arguments
│   └── ui
│       ├── theme            // M3 Color, Typography (Inter/Roboto), Theme
│       └── components       // Button, Card, TimerBadge, MathView, TopBar
│
├── data
│   ├── local
│   │   ├── dao              // QuestionDao, PackageDao, ExamResultDao
│   │   └── entity           // QuestionEntity, PackageEntity, ExamResultEntity
│   ├── remote
│   │   ├── api              // Paket & Soal remote endpoints
│   │   └── dto              // Response JSON DTO
│   └── repository           // Implementasi Repository (Offline-first sync)
│
├── domain
│   ├── model                // Question, Package, ExamSession, UserProfile
│   ├── repository           // Interface Repository
│   └── usecase
│       ├── exam             // StartExamUseCase, SubmitAnswerUseCase, CalculateScoreUseCase
│       ├── package_soal     // GetPackagesUseCase, DownloadPackageUseCase
│       └── billing          // UnlockPackageUseCase, CheckPremiumStatusUseCase
│
├── feature
│   ├── auth                 // Login Google, Profil Pengguna
│   ├── home                 // Dashboard: Kategori (SMP, SMA, UTBK, CPNS), Progres
│   ├── catalog              // Daftar paket soal, status (Gratis / Premium / Beli)
│   ├── exam                 // Layar Ujian CAT: Soal, Timer, Lembar Jawaban, Ragu-ragu
│   ├── result               // Hasil Skor, Analisis Kelulusan/Passing Grade
│   ├── review               // Pembahasan Soal & Kunci Jawaban
│   ├── billing              // Layar Langganan Premium & Toko Paket Soal
│   └── settings             // Dark Mode, Reset Cache Soal, Kebijakan Privasi
│
└── monetization
    ├── ads                  // AdMob Manager (Banner, Interstitial, Rewarded Helper)
    └── billing              // Google Play Billing Client Manager
```

---

## 4. Perancangan Skema Database (Room Entity)

### A. Entity Paket Soal (`exam_packages`)
```kotlin
@Entity(tableName = "exam_packages")
data class ExamPackageEntity(
    @PrimaryKey val id: String,                 // e.g. "cpns_skd_2026_paket_01"
    val categoryId: String,                     // "CPNS", "UTBK", "SMA", "SMP"
    val subCategory: String,                    // "SKD", "TPS", "TKA_SAINTEK"
    val title: String,                          // "Tryout Akbar SKD CPNS 2026 #1"
    val description: String,
    val totalQuestions: Int,
    val durationMinutes: Int,                   // Durasi ujian dalam menit
    val passingScore: Int = 0,                  // Nilai ambang batas jika ada
    val isFree: Boolean = false,                // Gratis untuk lead magnet
    val priceIdr: Long = 0,                     // Harga IAP jika berbayar satuan
    val isDownloaded: Boolean = false,          // Status cache lokal
    val version: Int = 1                        // Versi untuk update konten
)
```

### B. Entity Soal Fleksibel (`questions`)
```kotlin
@Entity(
    tableName = "questions",
    indices = [
        Index(value = ["packageId"]),
        Index(value = ["passageId"])
    ]
)
data class QuestionEntity(
    @PrimaryKey val id: String,                 // e.g. "cpns_tiu_001"
    val packageId: String,
    val passageId: String? = null,              // Relasi ke teks wacana jika ada
    val sectionName: String,                    // e.g. "TWK", "TIU", "TKP", "Matematika"
    val questionNumber: Int,
    val questionText: String,                   // Teks soal (mendukung Markdown/LaTeX)
    val imageUrl: String? = null,               // Diagram atau gambar pendukung
    
    // Opsi Jawaban (Mendukung hingga 5 opsi A-E)
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val optionE: String? = null,
    
    // Aturan Penilaian
    val scoringType: String = "STANDARD",       // "STANDARD", "TKP_WEIGHTED", "PENALTY"
    val correctAnswer: String,                  // "A", "B", "C", "D", atau "E"
    val scoreA: Int = 0,                        // Bobot poin opsi A (penting untuk TKP)
    val scoreB: Int = 0,
    val scoreC: Int = 0,
    val scoreD: Int = 0,
    val scoreE: Int = 0,
    
    val explanation: String,                    // Pembahasan lengkap
    val explanationImageUrl: String? = null
)
```

### C. Entity Teks Wacana / Bacaan (`passages`)
```kotlin
@Entity(tableName = "passages")
data class PassageEntity(
    @PrimaryKey val id: String,                 // e.g. "passage_utbk_bahasa_01"
    val title: String? = null,
    val content: String                         // Teks bacaan panjang untuk beberapa soal
)
```

### D. Entity Sesi Pengerjaan & Riwayat (`exam_attempts`)
```kotlin
@Entity(tableName = "exam_attempts")
data class ExamAttemptEntity(
    @PrimaryKey val attemptId: String,
    val packageId: String,
    val startedAt: Long,
    val finishedAt: Long? = null,
    val timeSpentSeconds: Long = 0,
    val totalScore: Double = 0.0,
    val isPassed: Boolean = false,
    val userAnswersJson: String,                // Map<QuestionId, UserAnswerState>
    val isSubmitted: Boolean = false
)
```

---

## 5. Engine Ujian & State Management (CAT Simulation)

Pola dari `QuizViewModel` di EduQuest diadaptasi menjadi **CAT Simulation Engine**:

### State Ujian (`ExamUiState`)
```kotlin
data class ExamUiState(
    val isLoading: Boolean = true,
    val packageTitle: String = "",
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<String, String> = emptyMap(),      // QuestionId -> Opsi Terpilih ("A")
    val flaggedQuestions: Set<String> = emptySet(),         // Soal yang ditandai "Ragu-ragu"
    val remainingTimeSeconds: Long = 0,
    val isTimeUp: Boolean = false,
    val isSubmitDialogVisible: Boolean = false,
    val currentPassage: Passage? = null
)
```

### Indikator Status Lembar Jawaban (Grid Navigation):
1. **Hijau:** Terjawab yakin.
2. **Kuning:** Ditandai ragu-ragu (*flagged*).
3. **Abu-abu / Putih:** Belum dijawab.
4. **Biru Border:** Soal yang sedang aktif dibuka.

---

## 6. Pola Integrasi Monetisasi

```
                                 [ Pengguna Membuka Aplikasi ]
                                               │
                                 ┌─────────────┴─────────────┐
                                 ▼                           ▼
                        [ User Gratis ]              [ User Premium ]
                                 │                           │
                   ┌─────────────┴─────────────┐             │
                   ▼                           ▼             │
            [ Banner Ad ]             [ Tryout Terkunci ]    │
          (Menu & Dashboard)                   │             │
                                    ┌──────────┴──────────┐  │
                                    ▼                     ▼  │
                             [ Beli Satuan ]     [ Langganan ]
                             (Play Billing)      (Play Billing)
                                    │                     │  │
                                    └──────────┬──────────┘  │
                                               ▼             ▼
                                     [ Akses Penuh Tanpa Iklan ]
```

1. **AdMob Helper:**
   - Memisahkan logika iklan dari UI Compose via Singleton `AdsManager`.
   - Menggunakan `RewardedAd` untuk memberi kesempatan user gratis membuka 1 paket latihan atau melihat pembahasan detail.
2. **Play Billing Helper:**
   - Menangani `PurchasesUpdatedListener`, konsumsi produk satuan (*inapp*), dan langganan bulanan (*subs*).
   - Sinkronisasi status akun ke Firebase Firestore agar saat ganti perangkat, paket tetap terbaca.

---

## 7. Rekomendasi Stack Dependensi (`libs.versions.toml`)

- **Kotlin:** `2.0.0`+
- **Compose Compiler:** Menggunakan default Compose Compiler Plugin Kotlin 2.0.
- **Material 3:** `androidx.compose.material3:material3:1.3.0`
- **Room Database:** `androidx.room:room-ktx:2.6.1`
- **Navigation:** `androidx.navigation:navigation-compose:2.8.0`
- **Play Billing:** `com.android.billingclient:billing-ktx:7.0.0`
- **Google Mobile Ads:** `com.google.android.gms:play-services-ads:23.3.0`
- **Firebase:** Firebase BoM (Auth, Firestore, Analytics)
- **Math/Formula Renderer:** `io.github.kexanie.library:MathView` atau integrasi KaTeX WebView ringan untuk rumus matematika/sains.

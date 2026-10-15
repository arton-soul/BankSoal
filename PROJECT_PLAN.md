# Rencana Kerja Terstruktur: Pengembangan Bank Soal Pro
**Roadmap, Tahapan Eksekusi, dan Panduan Implementasi Step-by-Step**

Dokumen ini memuat perencanaan kerja menyeluruh untuk membangun aplikasi **Bank Soal Pro** (SMP, SMA, UTBK/SNBT, CPNS/PPPK, dan Kedinasan) secara terstruktur, efisien, dan siap dimonetisasi.

---

## 📌 Ringkasan Roadmap & Milestone

| Fase | Nama Fase | Fokus Utama | Target Luaran (*Deliverable*) |
|:---:|---|---|---|
| **Fase 1** | **Fondasi & Inisialisasi** | Setup project, tema M3, arsitektur dasar, Room DB | Project ter-build, navigasi & DB siap |
| **Fase 2** | **Engine Simulasi Ujian (CAT)** | Timer, lembar jawaban, navigasi nomor, ragu-ragu | Simulasi CAT berfungsi penuh |
| **Fase 3** | **Hierarki Konten & Paket Soal** | Kategori (SMP, SMA, UTBK, CPNS), paket tryout, JSON parser | Paket soal tampil & bisa di-load offline |
| **Fase 4** | **Sistem Skoring, Review & Rumus** | Kalkulasi nilai (TKP bobot, minus, standar), pembahasan, rumus LaTeX | Hasil ujian & analisis passing grade siap |
| **Fase 5** | **Akun Pengguna & Cloud Sync** | Google Sign-In, Firebase Auth & Firestore | Profil user & hak akses tersimpan di cloud |
| **Fase 6** | **Implementasi Monetisasi** | AdMob (Banner, Interstitial, Rewarded) & Play Billing (IAP/Subs) | Alur pembelian & iklan berjalan stabil |
| **Fase 7** | **Testing, ASO & Persiapan Rilis** | Proguard/R8, unit testing, store listing, Play Console | AAB siap upload ke Google Play |

---

## 📋 Rincian Tahap Demi Tahap (Step-by-Step)

### FASE 1: Inisialisasi & Setup Arsitektur Dasar
**Tujuan:** Membangun kerangka aplikasi yang bersih, modern, dan scalable berdasarkan arsitektur EduQuest.

- [x] **Task 1.1: Project Setup**
  - Buat project Android baru (`com.banksoalpro.app`) dengan template *Empty Activity* (Compose).
  - Konfigurasi Gradle root dan app module (`build.gradle.kts`, `libs.versions.toml`) menggunakan Kotlin 2.0+ dan Java 17.
  - Setup dependensi: Compose Material 3, Navigation Compose, Room KTX, Coroutines, ViewModel KTX.
- [x] **Task 1.2: Design System & Theming**
  - Buat tema profesional formal: `BankSoalTheme` (Palet warna biru tua/navy cerdas, netral slate, aksen emas/emerald untuk status sukses).
  - Konfigurasi typography yang nyaman dibaca untuk teks soal panjang.
- [x] **Task 1.3: Database Setup (Room)**
  - Tulis entity: `ExamPackageEntity`, `QuestionEntity`, `PassageEntity`, `ExamAttemptEntity`.
  - Buat DAO dasar: `ExamPackageDao`, `QuestionDao`, `ExamAttemptDao`.
  - Inisialisasi `BankSoalDatabase` dan type converters (untuk list/map string).
- [x] **Task 1.4: Base Navigation Host**
  - Definisikan rute sealed class `Screen`: `Home`, `Catalog/{categoryId}`, `Exam/{packageId}`, `Result/{attemptId}`, `Review/{attemptId}`, `Subscription`, `Settings`.
  - Setup Scaffold utama dengan TopBar dan BottomNavigation jika diperlukan.

---

### FASE 2: Engine Simulasi Ujian (CAT Engine)
**Tujuan:** Membuat layar ujian interaktif berstandar CAT (BKN/UTBK) dengan performa tinggi dan bebas lag.

- [x] **Task 2.1: Model Domain Ujian & State**
  - Rancang `ExamUiState` (soal aktif, opsi terpilih, map ragu-ragu, sisa detik timer).
  - Buat `ExamViewModel` yang menangani *countdown timer* via Coroutine Flow.
  - Pengacakan opsi jawaban (Dynamic Option Shuffling) per sesi ujian agar pengguna tidak menghafal urutan abjad.
- [x] **Task 2.2: Komponen UI Soal (Question Card)**
  - Tampilan nomor soal, nama subtes (TIU/TWK/TKP/Matematika).
  - Teks soal dengan dukungan teks panjang dan gambar diagram.
  - Komponen opsi radio button modern (A, B, C, D, E).
- [x] **Task 2.3: Lembar Navigasi Kisi Nomor Soal (Grid Sheet)**
  - Bottom sheet / dialog yang menampilkan seluruh nomor soal (misal: 1–110).
  - Warna indikator real-time:
    - 🟩 Hijau = Terjawab
    - 🟨 Kuning = Ragu-ragu
    - ⬜ Abu-abu = Belum dikerjakan
  - Aksi *tap* nomor langsung melompat (*jump to question*).
- [x] **Task 2.4: Wacana / Teks Bacaan Panjang (Passage Viewer)**
  - Sediakan panel atau tab yang bisa di-*scroll* untuk membaca teks wacana panjang bagi soal pemahaman bacaan Bahasa / TPS.
- [x] **Task 2.5: Dialog Konfirmasi & Selesai Ujian**
  - Peringatan jumlah soal yang belum terjawab atau masih berstatus ragu-ragu sebelum submit akhir.
  - Fitur *auto-submit* ketika waktu habis (*timer runs out*).

---

### FASE 3: Manajemen Konten & Paket Soal
**Tujuan:** Pengelolaan ribuan soal secara terstruktur tanpa membuat ukuran APK membengkak.

- [x] **Task 3.1: Hierarki Kategori & Dashboard Utama**
  - Tampilan Menu Utama:
    - 🎓 **SMP:** Ujian Sekolah, Asesmen Nasional.
    - 🏫 **SMA:** Penilaian Akhir Semester, Ujian Sekolah.
    - 🏛️ **UTBK / SNBT:** Tes Potensi Skolastik (TPS), Literasi Bahasa, Penalaran Matematika.
    - 💼 **CPNS / PPPK:** Seleksi Kompetensi Dasar (TWK, TIU, TKP) & SKB.
    - 👮 **Kedinasan / TNI / POLRI:** Tes Akademik & Psikotes.
  - Integrasi data reaktif Kotlin Flow dari Room DB ke `CatalogScreen` & `CatalogViewModel`.
- [x] **Task 3.2: Format JSON Standar untuk Input Soal**
  - Buat format baku data soal dalam bentuk JSON (memudahkan penambahan soal secara massal dari Excel/Google Sheets).
  - DTO & Parser aman (`ExamPackageJsonDto.kt`, `ExamJsonParser.kt`) dan dokumentasi di `docs/QUESTION_JSON_SPECIFICATION.md`.
- [x] **Task 3.3: Paket Starter & Mekanisme Cache Offline / GitHub Content Delivery**
  - Sertakan 1 paket contoh gratis per jenjang di `assets/seed/` sebagai starter saat aplikasi pertama kali diinstal.
  - Buat service `ContentSyncManager` untuk mengunduh paket soal tambahan dan update katalog langsung dari GitHub repository pengguna (`arton-soul/BankSoal`).

---

### FASE 4: Mesin Skoring, Review & Rumus Sains
**Tujuan:** Menghitung skor secara akurat sesuai jenis ujian dan menampilkan pembahasan edukatif.

- [x] **Task 4.1: Multi-Rule Scoring Calculator**
  - **Aturan Standar:** Benar = 1 (atau poin tertentu), Salah = 0.
  - **Aturan TKP CPNS:** Bobot bertingkat 1 sampai 5 untuk setiap opsi (tidak ada salah).
  - **Aturan Penalti:** Benar = +4, Salah = -1, Kosong = 0 (untuk tes model lama/khusus).
  - Rincian kalkulasi per subtes (TWK min 65, TIU min 80, TKP min 166).
- [x] **Task 4.2: Layar Hasil Ujian (Result & Analytics)**
  - Tampilan total skor, status lolos/tidak lolos *Passing Grade*.
  - Rincian performa per subtes (TWK, TIU, TKP).
  - Durasi pengerjaan rata-rata per soal dan akurasi %.
- [x] **Task 4.3: Layar Pembahasan Mendalam (Review Mode)**
  - Menampilkan kembali semua nomor dengan warna: kunci jawaban benar (hijau) dan jawaban user jika salah (merah).
  - Teks penjelasan/pembahasan lengkap di bawah soal.
  - Fitur **Bookmark Soal** untuk menyimpan soal sulit ke daftar favorit.
  - Filter nomor: "Semua", "Salah", "Benar", "Kosong", "Disimpan".
- [x] **Task 4.4: Dukungan Rumus Matematika / Fisika (KaTeX / Math)**
  - Integrasikan komponen rendering LaTeX ringan / MathTextView agar rumus matematika, pecahan, dan integral tampil rapi.

---

### FASE 5: Akun Pengguna & Sinkronisasi Cloud
**Tujuan:** Menjaga riwayat dan hak kepemilikan paket yang dibeli agar tidak hilang saat pengguna berganti perangkat.

- [ ] **Task 5.1: Firebase Authentication (Google Sign-In)**
  - Tombol login sekali klik dengan akun Google.
  - Opsi *Masuk sebagai Tamu* (Guest) agar pengguna bisa mencoba tanpa hambatan registrasi.
- [ ] **Task 5.2: Cloud Entitlement Record (Firestore / Database)**
  - Tabel `user_entitlements`:
    - `userId`
    - `isPremiumUser: Boolean`
    - `unlockedPackageIds: List<String>`
    - `premiumExpiryDate: Timestamp?`
  - Sinkronisasi otomatis saat login berhasil.

---

### FASE 6: Implementasi Monetisasi (AdMob & Play Billing)
**Tujuan:** Menghasilkan pendapatan pasif dari iklan dan konversi pengguna berbayar.

- [ ] **Task 6.1: Integrasi Google Mobile Ads (AdMob)**
  - Setup App ID AdMob di `AndroidManifest.xml`.
  - Buat `AdsManager` singleton untuk memuat iklan secara *preloaded*:
    - **Banner Ad:** Di bagian bawah layar katalog paket dan dashboard.
    - **Interstitial Ad:** Tampil secara elegan hanya setelah layar hasil ujian ditutup (maksimal 1x per sesi ujian, jangan ganggu pengerjaan).
    - **Rewarded Video Ad:** Digunakan untuk membuka 1 pembahasan soal atau mencoba tryout premium gratis.
  - Pastikan iklan **mati total** jika user berstatus Premium.
- [ ] **Task 6.2: Integrasi Google Play Billing Library 7.0+**
  - Buat `BillingRepository` untuk menangani:
    - Inisialisasi koneksi ke Google Play Store.
    - Mengambil daftar produk & harga lokal (Rupiah).
    - Menangani proses pembelian (*Purchase Flow*).
    - Verifikasi pembelian dan `acknowledgePurchase`.
- [ ] **Task 6.3: Skema Produk Monetisasi**
  - **In-App Product (Sekali Beli):**
    - `pkg_cpns_master_2026`: Rp29.000
    - `pkg_utbk_lengkap_2026`: Rp39.000
    - `pkg_sma_smp_all`: Rp25.000
  - **Subscription (Langganan):**
    - `sub_pro_monthly`: Rp19.000/bulan (Akses semua paket + Tanpa Iklan)
    - `sub_pro_yearly`: Rp99.000/tahun
- [ ] **Task 6.4: Layar Penawaran Premium (Paywall Screen)**
  - Tampilan visual benefit Premium (Bebas Iklan, Akses Ribuan Soal, Kunci Pembahasan Lengkap, Simulasi CAT Tanpa Batas).

---

### FASE 7: Pengujian, Optimasi & Publikasi
**Tujuan:** Menyiapkan rilis aplikasi yang stabil, cepat, dan teroptimasi ASO di Play Store.

- [ ] **Task 7.1: Keamanan & Proguard / R8**
  - Konfigurasi `proguard-rules.pro` untuk Room, Firebase, dan Play Billing.
  - Enkripsi data lokal sensitif jika diperlukan.
- [ ] **Task 7.2: Pengujian Komprehensif**
  - Unit test logika skoring (terutama TKP dan passing grade).
  - Uji skenario offline (pengerjaan ujian saat koneksi internet mati).
  - Uji skenario transaksi Play Billing sandbox / test license.
- [ ] **Task 7.3: Persiapan ASO (App Store Optimization) & Store Assets**
  - Ikon aplikasi profesional (format SVG / PNG 512x512).
  - Feature graphic (1024x500).
  - Screenshot mockup beresolusi tinggi yang menonjolkan fitur CAT, Bank Soal, dan Pembahasan.
  - Pasang teks ASO yang telah dirancang:
    - *Title:* `BankSoal.id: Tryout & Ujian` atau `Ujianku: Bank Soal & Tryout`
    - *Short Description:* Memuat keyword CPNS, UTBK, SMA, SMP, dan CAT.
- [ ] **Task 7.4: Build Release & Google Play Console Submission**
  - Generate Release Signing Key (.jks).
  - Build Android App Bundle (`bundleRelease`).
  - Upload ke Internal / Closed Testing track untuk uji coba awal.

---

## 🛠️ Tips Efisiensi Pembuatan Konten Soal Massal

Salah satu tantangan terbesar aplikasi Bank Soal adalah proses input ribuan soal.
Gunakan alur berikut untuk menghemat waktu:
1. **Gunakan Google Spreadsheet / Excel** dengan kolom:
   `kategori`, `subtes`, `soal`, `opsi_a`, `opsi_b`, `opsi_c`, `opsi_d`, `opsi_e`, `kunci`, `bobot_a_sd_e`, `pembahasan`.
2. Buat script Python sederhana (atau generator online) untuk mengekspor Google Sheet langsung menjadi file `.json`.
3. Unggah file JSON tersebut ke cloud storage atau letakkan di folder assets aplikasi.

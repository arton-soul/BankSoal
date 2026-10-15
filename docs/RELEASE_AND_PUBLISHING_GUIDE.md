# Panduan Build Rilis, Signing & Publikasi Google Play Store
**Aplikasi:** Bank Soal Pro  
**Maintainer / Developer:** `arton.79@gmail.com`  
**GitHub Content Repository:** `https://github.com/arton-soul/BankSoal.git`

Dokumen ini memandu proses pembuatan signing key rilis, build Android App Bundle (.aab), konfigurasi Google Play Console, hingga alur pembaruan konten bank soal secara online melalui GitHub.

---

## 1. Pembuatan Release Signing Keystore (.jks)

Untuk mempublikasikan aplikasi ke Google Play Store, Anda memerlukan keystore rilis pribadi.

### Jalankan perintah `keytool` melalui terminal:
```bash
keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias banksoal
```

> [!CAUTION]
> **PENTING:** Simpan file `release-key.jks` dan catat password keystore di tempat yang aman (misalnya password manager). Jangan mengunggah file `.jks` ke GitHub publik. File `.jks` sudah otomatis terdaftar di `.gitignore`.

---

## 2. Konfigurasi `keystore.properties`

Buat file baru bernama `keystore.properties` di root folder proyek (`d:\Android\BankSoalPro\keystore.properties`) dengan format:

```properties
storeFile=release-key.jks
storePassword=PASSWORD_KEYSTORE_ANDA
keyAlias=banksoal
keyPassword=PASSWORD_KEY_ANDA
```

Sistem Gradle di `app/build.gradle.kts` telah dikonfigurasi untuk:
- Otomatis mendeteksi `keystore.properties` jika tersedia dan menandatangani build rilis dengan kunci resmi tersebut.
- Otomatis fallback ke signing debug jika file ini belum ada (sehingga pengujian lokal atau CI tetap berjalan lancar tanpa error).

---

## 3. Menghasilkan Android App Bundle (.aab)

Google Play Store mewajibkan format **Android App Bundle (.aab)** untuk rilis aplikasi baru:

### Perintah Build:
```powershell
.\gradlew.bat bundleRelease
```

File output `.aab` yang telah dioptimasi dengan Proguard/R8 dan resource shrinking akan dihasilkan di:
```text
app/build/outputs/bundle/release/app-release.aab
```

Jika ingin membuat APK untuk pengujian langsung di perangkat fisik:
```powershell
.\gradlew.bat assembleRelease
```
Output APK berada di: `app/build/outputs/apk/release/app-release.apk`

---

## 4. Konfigurasi Google Play Console

### Langkah 1: Buat Aplikasi Baru
1. Buka [Google Play Console](https://play.google.com/console).
2. Klik **Create App** (Buat Aplikasi).
3. Masukkan nama: `Bank Soal: Tryout CPNS & UTBK` (maks 30 karakter).
4. Pilih Bahasa default: **Indonesian (id)**.
5. Jenis aplikasi: **App**, Free with In-App Purchases.

### Langkah 2: Isi Store Listing & ASO
Gunakan materi lengkap yang telah disiapkan di file:
👉 [`docs/ASO_AND_STORE_LISTING.md`](file:///d:/Android/BankSoalPro/docs/ASO_AND_STORE_LISTING.md)
- Salin Judul, Deskripsi Singkat, dan Deskripsi Lengkap.
- Unggah Ikon Aplikasi (512x512) dan Feature Graphic (1024x500).
- Unggah minimal 4 screenshot ponsel.

### Langkah 3: Pengaturan Monetisasi (Produk Dalam Aplikasi)
Buka menu **Monetize > Products > In-App Products** dan **Subscriptions**:

1. **In-App Products (Sekali Beli):**
   - Product ID: `pkg_cpns_master_2026` | Nama: *Paket Master CPNS SKD 2026* | Harga: Rp29.000
   - Product ID: `pkg_utbk_lengkap_2026` | Nama: *Paket Master UTBK-SNBT 2026* | Harga: Rp39.000
   - Product ID: `pkg_sma_smp_all` | Nama: *Paket Komplit SMA & SMP* | Harga: Rp25.000

2. **Subscriptions (Langganan PRO):**
   - Product ID: `sub_pro_monthly` | Nama: *Bank Soal PRO (Bulanan)* | Harga: Rp19.000 / bulan
   - Product ID: `sub_pro_yearly` | Nama: *Bank Soal PRO (Tahunan)* | Harga: Rp99.000 / tahun

### Langkah 4: Pengaturan Iklan & AdMob (App-ads.txt)
- AdMob App ID yang terpasang: `ca-app-pub-4098008677804088~2352896167`
- Hubungkan aplikasi di konsol Google AdMob ke Google Play Store setelah listing dibuat.
- Tambahkan baris `google.com, pub-4098008677804088, DIRECT, f08c47fec0942fa0` ke file `app-ads.txt` di domain web pengembang Anda.

### Langkah 5: Unggah App Bundle & Jalankan Closed Testing
1. Buka menu **Testing > Closed testing** (atau Internal testing).
2. Buat rilis baru dan unggah file `app-release.aab`.
3. Masukkan Release Notes (misal: *“Rilis perdana Bank Soal Pro: Simulasi CAT CPNS, UTBK, SMA & SMP”*).
4. Review dan rilis ke penguji internal.

---

## 5. Panduan Pembaruan Konten Soal via GitHub (Remote Sync)

Aplikasi Bank Soal Pro memiliki fitur sinkronisasi cerdas (`ContentSyncManager.kt`) yang memungkinkan Anda menambahkan ribuan soal baru tanpa perlu merilis update APK baru di Play Store!

### Alur Update Konten:
1. Siapkan soal baru dalam format JSON sesuai panduan di [`docs/QUESTION_JSON_SPECIFICATION.md`](file:///d:/Android/BankSoalPro/docs/QUESTION_JSON_SPECIFICATION.md).
2. Buat atau perbarui file JSON di repository: `https://github.com/arton-soul/BankSoal.git` pada branch `main`.
3. Perbarui `catalog.json` di root repository tersebut:
   ```json
   {
     "catalogVersion": 2,
     "lastUpdated": "2026-10-15T20:00:00Z",
     "packages": [
       {
         "id": "cpns_skd_2026_batch2",
         "title": "Simulasi SKD CPNS 2026 - Paket 2",
         "category": "CPNS",
         "subCategory": "SKD (TWK, TIU, TKP)",
         "totalQuestions": 110,
         "durationMinutes": 100,
         "passingScore": 311,
         "isPremium": false,
         "version": 1,
         "downloadUrl": "https://raw.githubusercontent.com/arton-soul/BankSoal/main/packages/cpns_skd_2026_batch2.json"
       }
     ]
   }
   ```
4. Saat pengguna membuka katalog atau menarik layar untuk refresh (*swipe to refresh*), aplikasi secara otomatis membandingkan versi katalog, mengunduh soal baru, dan menyimpannya di Room Database lokal sehingga bisa dikerjakan secara offline.

# Panduan Format JSON & Manajemen Konten Massal

Dokumen ini adalah spesifikasi baku untuk mengelola dan mengunggah ribuan butir soal ke repository GitHub [BankSoal](https://github.com/arton-soul/BankSoal.git) agar aplikasi Bank Soal Pro dapat mengunduh pembaruan secara otomatis.

---

## 1. Struktur Repository di GitHub

```text
https://github.com/arton-soul/BankSoal/
│
├── catalog.json                 // Indeks seluruh paket soal & versinya
│
└── packages/                    // Folder paket soal lengkap
    ├── cpns_skd_free_01.json
    ├── cpns_skd_master_02.json
    ├── utbk_snbt_free_01.json
    ├── sma_sample_01.json
    └── ...
```

---

## 2. Format `catalog.json`

File `catalog.json` diletakkan di root repository GitHub. Setiap kali ada penambahan paket atau update soal, naikkan nilai `"version"`.

```json
{
  "version": 1,
  "updatedAt": "2026-10-15T00:00:00Z",
  "packages": [
    {
      "id": "cpns_skd_free_01",
      "categoryId": "CPNS",
      "subCategory": "SKD CPNS",
      "title": "Tryout Akbar SKD CPNS 2026 (Starter)",
      "description": "Simulasi lengkap materi TWK, TIU, dan TKP sesuai kisi-kisi MenPAN-RB terbaru.",
      "totalQuestions": 110,
      "durationMinutes": 100,
      "passingScore": 311,
      "isFree": true,
      "priceIdr": 0,
      "version": 1,
      "downloadUrl": "packages/cpns_skd_free_01.json"
    }
  ]
}
```

---

## 3. Format File Paket (`packages/{package_id}.json`)

Setiap file paket memuat 3 bagian: `package` (metadata), `passages` (teks wacana panjang opsional), dan `questions` (daftar butir soal).

```json
{
  "package": {
    "id": "cpns_skd_free_01",
    "categoryId": "CPNS",
    "subCategory": "SKD CPNS",
    "title": "Tryout Akbar SKD CPNS 2026 (Starter)",
    "description": "Simulasi lengkap...",
    "totalQuestions": 110,
    "durationMinutes": 100,
    "passingScore": 311,
    "isFree": true,
    "priceIdr": 0,
    "version": 1,
    "downloadUrl": "packages/cpns_skd_free_01.json"
  },
  "passages": [
    {
      "id": "passage_01",
      "title": "Judul Wacana Bacaan",
      "content": "Teks wacana panjang untuk beberapa nomor soal sekaligus..."
    }
  ],
  "questions": [
    {
      "id": "cpns_twk_01",
      "packageId": "cpns_skd_free_01",
      "passageId": null,
      "sectionName": "TWK",
      "questionNumber": 1,
      "questionText": "Teks pertanyaan soal...",
      "imageUrl": null,
      "optionA": "Opsi pilihan A",
      "optionB": "Opsi pilihan B",
      "optionC": "Opsi pilihan C",
      "optionD": "Opsi pilihan D",
      "optionE": "Opsi pilihan E",
      "scoringType": "STANDARD",
      "correctAnswer": "C",
      "scoreA": 0,
      "scoreB": 0,
      "scoreC": 5,
      "scoreD": 0,
      "scoreE": 0,
      "explanation": "Pembahasan lengkap mengenai nomor ini..."
    },
    {
      "id": "cpns_tkp_01",
      "packageId": "cpns_skd_free_01",
      "passageId": null,
      "sectionName": "TKP",
      "questionNumber": 2,
      "questionText": "Pertanyaan TKP dengan bobot 1-5 poin...",
      "imageUrl": null,
      "optionA": "Opsi A",
      "optionB": "Opsi B",
      "optionC": "Opsi C",
      "optionD": "Opsi D",
      "optionE": "Opsi E",
      "scoringType": "TKP_WEIGHTED",
      "correctAnswer": "B",
      "scoreA": 3,
      "scoreB": 5,
      "scoreC": 2,
      "scoreD": 4,
      "scoreE": 1,
      "explanation": "Penjelasan nilai bobot opsi..."
    }
  ]
}
```

---

## 4. Tipe Penilaian (`scoringType`)
1. **`STANDARD`**:
   - Benar mendapat poin (sesuai bobot `scoreA..scoreE` atau default 5/1).
   - Salah mendapat poin 0.
2. **`TKP_WEIGHTED`**:
   - Khusus Tes Karakteristik Pribadi CPNS/Kedinasan.
   - Setiap pilihan bernilai 1 hingga 5 poin (`scoreA` s/d `scoreE`).
   - Tidak ada pengurangan nilai.
3. **`PENALTY`**:
   - Benar = +4, Salah = -1, Kosong = 0 (untuk tes model SBMPTN lama).

---

## 5. Alur Pembaruan Konten ke Aplikasi Pengguna
1. Admin membuat soal di Google Sheet / Excel.
2. Ekspor/konversi data sheet menjadi file `.json` dengan format di atas.
3. Unggah file JSON ke GitHub repository `https://github.com/arton-soul/BankSoal.git`.
4. Perbarui file `catalog.json` di GitHub (naikkan nomor `"version"`).
5. Pengguna di aplikasi menekan tombol **Perbarui dari GitHub** pada katalog (atau aplikasi mengeceknya secara berkala):
   - Aplikasi otomatis mendeteksi versi paket baru dan mengunduh pembaruan langsung ke database Room offline di ponsel pengguna.

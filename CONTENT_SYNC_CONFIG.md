# Konfigurasi Sinkronisasi & Metadata Konten

Dokumen ini mencatat konfigurasi repository dan metadata untuk sinkronisasi update konten Bank Soal Pro.

## Informasi Akun & Repository
- **Email Pengelola / Developer:** `arton.79@gmail.com`
- **Repository Konten & Sumber Soal:** [BankSoal GitHub](https://github.com/arton-soul/BankSoal.git)
- **Base URL Raw GitHub:** `https://raw.githubusercontent.com/arton-soul/BankSoal/main/`

## Strategi Pembaruan Konten (GitHub-Driven Content Delivery)
Aplikasi **Bank Soal Pro** didesain dengan arsitektur *Hybrid / Offline-First*:
1. **Paket Starter (Bawaan):** Disertakan di dalam folder `assets/seed/` agar aplikasi langsung dapat dipakai begitu diinstal tanpa internet.
2. **Katalog & Versi Index (`catalog.json`):**
   - Disimpan di root repository GitHub: `https://raw.githubusercontent.com/arton-soul/BankSoal/main/catalog.json`
   - Berisi daftar paket soal, kategori (SMP, SMA, UTBK, CPNS, Kedinasan), status gratis/premium, versi paket, dan hash/URL download.
3. **Penyimpanan Paket Soal (`packages/{package_id}.json`):**
   - Setiap paket soal diunggah dalam format JSON terstruktur ke repository GitHub.
   - Contoh: `https://raw.githubusercontent.com/arton-soul/BankSoal/main/packages/cpns_skd_2026_paket_01.json`
4. **Mekanisme Update di Aplikasi:**
   - Aplikasi mengecek `catalog.json` secara berkala (atau saat user menekan tombol "Perbarui Soal").
   - Jika versi paket di GitHub lebih baru daripada versi di database Room lokal pengguna, aplikasi akan mengunduh paket tersebut dan memperbarui Room DB secara otomatis.

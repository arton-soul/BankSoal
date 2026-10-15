# Catatan Rilis Google Play Store (Release Notes)
**Aplikasi:** Bank Soal Pro  
**Package Name:** `com.banksoalpro.app`  
**Batas Karakter Google Play Console:** Maksimal 500 karakter per bahasa.

Dokumen ini berisi catatan rilis resmi untuk setiap versi aplikasi yang siap disalin langsung ke kolom **Release notes** di Google Play Console saat membuat rilis baru.

---

## 📌 Versi 1.0.0 (Build 1) - Rilis Perdana (Current Version)
*Tanggal Rilis: Oktober 2026*  
*Target Track: Internal Testing / Closed Testing / Production*

### Bahasa Indonesia (`id-ID`)
```text
Rilis perdana Bank Soal Pro:
• Simulasi CAT resmi: CPNS (SKD), PPPK, UTBK-SNBT, Kedinasan, SMA & SMP.
• Fitur Anti-Hafal: Pengacakan pilihan opsi jawaban (A, B, C, D, E) secara dinamis tiap sesi ujian.
• Skoring akurat: Standar Passing Grade BKN (TWK, TIU, TKP) & pembobotan UTBK.
• Pembahasan lengkap & rendering rumus matematika KaTeX tajam.
• 100% Offline Ready setelah paket diunduh.
• Sinkronisasi otomatis konten soal baru via GitHub.
```

### English (`en-US`)
```text
Initial release of Bank Soal Pro:
• Realistic CAT exam simulation: CPNS, PPPK, UTBK-SNBT, Kedinasan, High School & Middle School.
• Anti-memorization engine: Dynamic answer choices randomization (A-E) on every attempt.
• Accurate official scoring: BKN Passing Grade (TWK, TIU, TKP) & UTBK penalty rules.
• In-depth explanations with crisp KaTeX math & science formula rendering.
• 100% offline study mode.
• Seamless online question sync via GitHub.
```

---

## 📋 Template Catatan Rilis untuk Versi Selanjutnya

Gunakan format terstruktur di bawah ini setiap kali Anda merilis pembaruan versi baru di Google Play Console:

### Template Versi Pembaruan Minor / Konten Baru:
```text
Pembaruan Bank Soal Pro (v1.x.x):
• Penambahan ratusan paket soal latihan baru: [Sebutkan kategori, misal: SKD CPNS 2026 Batch 2 & TPS UTBK].
• Pembaruan kunci jawaban dan pembahasan materi yang lebih mendalam.
• Peningkatan kecepatan render rumus matematika dan navigasi lembar jawab.
• Perbaikan bug minor dan optimalisasi performa aplikasi saat offline.
```

### Format XML (Jika menggunakan Fastlane / Android Publisher API):
```xml
<id-ID>
Pembaruan Bank Soal Pro:
• Paket soal baru: CPNS SKD 2026 & UTBK SNBT.
• Pembahasan lebih lengkap dengan trik cepat.
• Peningkatan stabilitas dan performa simulasi ujian offline.
</id-ID>
<en-US>
Bank Soal Pro update:
• Added new exam packages: CPNS SKD & UTBK SNBT.
• Enhanced in-depth explanations with quick tricks.
• Stability and performance improvements for offline exam mode.
</en-US>
```

---

## 💡 Panduan Penulisan Catatan Rilis Efektif di Play Store

1. **Fokus pada Manfaat Pengguna (User-Centric):**
   - Utamakan menyebutkan penambahan soal baru, subtes baru, atau materi yang relevan dengan jadwal ujian nasional terdekat.
2. **Singkat, Padat & Menggunakan Bullet Points:**
   - Gunakan tanda peluru (`•`) agar mudah dibaca sekilas oleh pengguna di layar ponsel.
3. **Patuhi Batas Karakter:**
   - Pastikan teks tidak melebihi 500 karakter agar tidak terpotong atau ditolak oleh formulir Google Play Console.
4. **Hindari Kata Teknis Internal:**
   - Alih-alih menulis *"Refactor Room DAO query"*, gunakan *"Peningkatan kecepatan membuka riwayat dan pembahasan soal"*.

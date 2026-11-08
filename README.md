# KantongKu – Pencatatan Keuangan Konsep Kantong

Gaji masuk -> otomatis terbagi ke kantong sesuai persentase.

## Cara membuka
1. Ekstrak zip, buka folder `KantongKu` lewat **Android Studio** (File > Open).
2. Tunggu Gradle sync (butuh internet, JDK 17 bawaan Android Studio).
3. Jalankan (Run) di emulator / HP (minSdk 24).

## Ketentuan dari video (Pemrograman Mobile P10 – SharedPreferences)
| Ketentuan di video | Penerapan |
|---|---|
| SharedPreferences untuk data kecil, key-value | `PrefsManager`: nama panggilan, tema gelap, gaji terakhir |
| Room DB untuk banyak baris & perlu query | Tabel `pockets` (kantong) dan `transactions` (riwayat) |
| Jangan simpan data rahasia (kata sandi) | Tidak ada kata sandi/PIN yang disimpan |
| Java, kelas `PrefsManager`, `prefs.edit().putString(...).apply()` | Sama persis |
| `MODE_PRIVATE` | File `kantong_prefs.xml` privat aplikasi |
| `apply()` di UI, bukan `commit()` | Semua tulis prefs memakai `apply()` |
| Pengaturan tetap tersimpan saat aplikasi dibuka lagi | Nama & tema dibaca ulang di `App` / `MainActivity.onResume` |

## Fitur
- Gaji masuk -> dibagi otomatis (default: Kebutuhan 50%, Tabungan 20%, Dana Darurat 15%, Hiburan 15%).
- Total persentase harus 100%; sisa pembulatan masuk ke kantong pertama.
- Pengeluaran per kantong (saldo dicek), pindah saldo antar kantong, riwayat transaksi.
- Tambah / ubah / hapus kantong di Pengaturan (hapus hanya jika saldo 0).

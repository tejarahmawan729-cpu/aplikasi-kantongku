package com.kantongku.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Menyimpan DATA KECIL berbentuk key-value (sesuai materi P10):
 *  - nama panggilan
 *  - tema gelap
 *  - nominal gaji terakhir (kemudahan input)
 *
 * TIDAK menyimpan data rahasia (kata sandi, PIN, dsb).
 * Data banyak baris (kantong & riwayat transaksi) disimpan di Room DB.
 * File: shared_prefs/kantong_prefs.xml  (MODE_PRIVATE = privat untuk aplikasi ini)
 */
public class PrefsManager {
    private static final String FILE = "kantong_prefs";
    private static final String KEY_NAMA = "nama_panggilan";
    private static final String KEY_TEMA = "tema_gelap";
    private static final String KEY_GAJI = "gaji_terakhir";

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public String getNama() {
        return prefs.getString(KEY_NAMA, "");
    }

    public void setNama(String nama) {
        // apply(): tulis ke disk di thread latar, UI langsung lanjut
        prefs.edit().putString(KEY_NAMA, nama).apply();
    }

    public boolean isTemaGelap() {
        return prefs.getBoolean(KEY_TEMA, false);
    }

    public void setTemaGelap(boolean gelap) {
        prefs.edit().putBoolean(KEY_TEMA, gelap).apply();
    }

    public long getGajiTerakhir() {
        return prefs.getLong(KEY_GAJI, 0L);
    }

    public void setGajiTerakhir(long gaji) {
        prefs.edit().putLong(KEY_GAJI, gaji).apply();
    }
}

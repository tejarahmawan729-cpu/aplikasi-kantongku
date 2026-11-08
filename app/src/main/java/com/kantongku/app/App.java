package com.kantongku.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Tema gelap dibaca dari SharedPreferences setiap aplikasi dibuka -> pengaturan tetap tersimpan
        PrefsManager prefs = new PrefsManager(this);
        AppCompatDelegate.setDefaultNightMode(prefs.isTemaGelap()
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }
}

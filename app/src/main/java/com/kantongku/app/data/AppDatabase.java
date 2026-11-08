package com.kantongku.app.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Pocket.class, Txn.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PocketDao pocketDao();

    public abstract TxnDao txnDao();

    private static volatile AppDatabase instance;

    public static AppDatabase get(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(), AppDatabase.class, "kantong.db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    // Kantong bawaan (total 100%), bisa diubah di Pengaturan
                                    db.execSQL("INSERT INTO pockets(name, percent, balance) VALUES('Kebutuhan', 50, 0)");
                                    db.execSQL("INSERT INTO pockets(name, percent, balance) VALUES('Tabungan', 20, 0)");
                                    db.execSQL("INSERT INTO pockets(name, percent, balance) VALUES('Dana Darurat', 15, 0)");
                                    db.execSQL("INSERT INTO pockets(name, percent, balance) VALUES('Hiburan', 15, 0)");
                                }
                            })
                            .build();
                }
            }
        }
        return instance;
    }
}

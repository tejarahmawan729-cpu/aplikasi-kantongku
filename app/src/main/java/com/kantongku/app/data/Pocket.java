package com.kantongku.app.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/** Satu kantong: nama, persentase pembagian gaji, dan saldo (rupiah). */
@Entity(tableName = "pockets")
public class Pocket {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public int percent;
    public long balance;

    public Pocket() {
    }

    @Ignore
    public Pocket(String name, int percent) {
        this.name = name;
        this.percent = percent;
        this.balance = 0;
    }
}

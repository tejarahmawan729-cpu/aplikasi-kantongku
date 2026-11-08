package com.kantongku.app.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/** Riwayat transaksi. Nama kantong disalin agar riwayat tetap terbaca walau kantong dihapus. */
@Entity(tableName = "transactions")
public class Txn {
    public static final String GAJI = "GAJI";
    public static final String KELUAR = "KELUAR";
    public static final String TF_MASUK = "TF_MASUK";
    public static final String TF_KELUAR = "TF_KELUAR";

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long pocketId;
    public String pocketName;
    public String type;
    public long amount;
    public String note;
    public long createdAt;

    public Txn() {
    }

    @Ignore
    public Txn(long pocketId, String pocketName, String type, long amount, String note) {
        this.pocketId = pocketId;
        this.pocketName = pocketName;
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.createdAt = System.currentTimeMillis();
    }

    public boolean isMasuk() {
        return GAJI.equals(type) || TF_MASUK.equals(type);
    }
}

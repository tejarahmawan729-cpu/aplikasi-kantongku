package com.kantongku.app.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Semua operasi tulis berjalan di thread latar dan dalam satu transaksi database,
 * sehingga saldo kantong dan riwayat selalu konsisten.
 */
public class Repository {

    public interface Callback {
        /** error == null berarti sukses. Dipanggil di main thread. */
        void onResult(String error);
    }

    private static class BizException extends RuntimeException {
        BizException(String msg) {
            super(msg);
        }
    }

    private final AppDatabase db;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public Repository(Context context) {
        db = AppDatabase.get(context);
    }

    public LiveData<List<Pocket>> pockets() {
        return db.pocketDao().observeAll();
    }

    public LiveData<List<Txn>> history() {
        return db.txnDao().observeAll();
    }

    private void run(Runnable work, Callback cb) {
        io.execute(() -> {
            String error = null;
            try {
                db.runInTransaction(work);
            } catch (BizException e) {
                error = e.getMessage();
            } catch (Exception e) {
                error = "Terjadi kesalahan: " + e.getMessage();
            }
            final String result = error;
            main.post(() -> cb.onResult(result));
        });
    }

    /** Gaji masuk -> otomatis dibagi ke semua kantong sesuai persentase. */
    public void gajiMasuk(long amount, Callback cb) {
        run(() -> {
            if (amount <= 0) throw new BizException("Nominal gaji harus lebih dari 0");
            List<Pocket> list = db.pocketDao().getAll();
            if (list.isEmpty()) throw new BizException("Belum ada kantong. Tambahkan di Pengaturan.");
            int sum = 0;
            for (Pocket p : list) sum += p.percent;
            if (sum != 100) {
                throw new BizException("Total persentase kantong harus 100% (sekarang " + sum + "%). Atur di Pengaturan.");
            }
            long[] share = new long[list.size()];
            long used = 0;
            for (int i = 0; i < list.size(); i++) {
                share[i] = amount * list.get(i).percent / 100;
                used += share[i];
            }
            share[0] += amount - used; // sisa pembulatan masuk ke kantong pertama
            for (int i = 0; i < list.size(); i++) {
                Pocket p = list.get(i);
                if (share[i] == 0) continue;
                db.pocketDao().addBalance(p.id, share[i]);
                db.txnDao().insert(new Txn(p.id, p.name, Txn.GAJI, share[i],
                        "Gaji masuk (" + p.percent + "%)"));
            }
        }, cb);
    }

    public void pengeluaran(long pocketId, long amount, String note, Callback cb) {
        run(() -> {
            if (amount <= 0) throw new BizException("Nominal harus lebih dari 0");
            Pocket p = db.pocketDao().getById(pocketId);
            if (p == null) throw new BizException("Kantong tidak ditemukan");
            if (p.balance < amount) throw new BizException("Saldo kantong " + p.name + " tidak cukup");
            db.pocketDao().addBalance(p.id, -amount);
            db.txnDao().insert(new Txn(p.id, p.name, Txn.KELUAR, amount,
                    note == null || note.trim().isEmpty() ? "Pengeluaran" : note.trim()));
        }, cb);
    }

    public void transfer(long fromId, long toId, long amount, Callback cb) {
        run(() -> {
            if (amount <= 0) throw new BizException("Nominal harus lebih dari 0");
            if (fromId == toId) throw new BizException("Kantong asal dan tujuan harus berbeda");
            Pocket from = db.pocketDao().getById(fromId);
            Pocket to = db.pocketDao().getById(toId);
            if (from == null || to == null) throw new BizException("Kantong tidak ditemukan");
            if (from.balance < amount) throw new BizException("Saldo kantong " + from.name + " tidak cukup");
            db.pocketDao().addBalance(from.id, -amount);
            db.pocketDao().addBalance(to.id, amount);
            db.txnDao().insert(new Txn(from.id, from.name, Txn.TF_KELUAR, amount, "Pindah ke " + to.name));
            db.txnDao().insert(new Txn(to.id, to.name, Txn.TF_MASUK, amount, "Dari " + from.name));
        }, cb);
    }

    /** id == 0 -> kantong baru; selain itu ubah nama & persentase (saldo tidak berubah). */
    public void simpanKantong(long id, String name, int percent, Callback cb) {
        run(() -> {
            if (name == null || name.trim().isEmpty()) throw new BizException("Nama kantong wajib diisi");
            if (percent < 0 || percent > 100) throw new BizException("Persentase harus 0-100");
            if (id == 0) {
                db.pocketDao().insert(new Pocket(name.trim(), percent));
            } else {
                db.pocketDao().updateInfo(id, name.trim(), percent);
            }
        }, cb);
    }

    public void hapusKantong(long id, Callback cb) {
        run(() -> {
            Pocket p = db.pocketDao().getById(id);
            if (p == null) return;
            if (p.balance != 0) {
                throw new BizException("Saldo kantong masih ada. Pindahkan dulu ke kantong lain.");
            }
            db.pocketDao().deleteById(id);
        }, cb);
    }
}

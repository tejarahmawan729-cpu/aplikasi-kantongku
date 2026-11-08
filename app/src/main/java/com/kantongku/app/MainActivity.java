package com.kantongku.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kantongku.app.data.Pocket;
import com.kantongku.app.data.Repository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PrefsManager prefs;
    private Repository repo;
    private TextView tvSapaan, tvTotal;
    private PocketAdapter adapter;
    private final List<Pocket> current = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new PrefsManager(this);
        repo = new Repository(this);
        tvSapaan = findViewById(R.id.tvSapaan);
        tvTotal = findViewById(R.id.tvTotal);

        RecyclerView rv = findViewById(R.id.rvKantong);
        adapter = new PocketAdapter(null);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        repo.pockets().observe(this, list -> {
            current.clear();
            if (list != null) current.addAll(list);
            adapter.submit(list);
            long total = 0;
            for (Pocket p : current) total += p.balance;
            tvTotal.setText(Ui.rupiah(total));
        });

        findViewById(R.id.btnGaji).setOnClickListener(v -> dialogGaji());
        findViewById(R.id.btnKeluar).setOnClickListener(v -> dialogPengeluaran());
        findViewById(R.id.btnTransfer).setOnClickListener(v -> dialogTransfer());

        if (prefs.getNama().isEmpty()) dialogNama();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // nama panggilan dibaca dari SharedPreferences -> tetap ada setelah aplikasi dibuka lagi
        String nama = prefs.getNama();
        tvSapaan.setText(nama.isEmpty() ? "Halo!" : "Halo, " + nama + "!");
    }

    // ---------- dialog ----------

    private void dialogNama() {
        EditText et = Ui.field(this, "Nama panggilan", false);
        LinearLayout col = Ui.column(this);
        col.addView(et);
        new AlertDialog.Builder(this)
                .setTitle("Siapa nama panggilanmu?")
                .setView(col)
                .setPositiveButton("Simpan", (d, w) -> {
                    prefs.setNama(et.getText().toString().trim());
                    onResume();
                })
                .setNegativeButton("Nanti", null)
                .show();
    }

    private void dialogGaji() {
        if (current.isEmpty()) {
            Toast.makeText(this, "Belum ada kantong. Tambahkan di Pengaturan.", Toast.LENGTH_LONG).show();
            return;
        }
        LinearLayout col = Ui.column(this);
        EditText et = Ui.field(this, "Nominal gaji (Rp)", true);
        if (prefs.getGajiTerakhir() > 0) et.setText(String.valueOf(prefs.getGajiTerakhir()));
        TextView info = new TextView(this);
        StringBuilder sb = new StringBuilder("Akan dibagi otomatis:\n");
        for (Pocket p : current) sb.append("• ").append(p.name).append(" ").append(p.percent).append("%\n");
        info.setText(sb.toString().trim());
        info.setPadding(0, Ui.dp(this, 12), 0, 0);
        col.addView(et);
        col.addView(info);

        new AlertDialog.Builder(this)
                .setTitle("Gaji masuk")
                .setView(col)
                .setPositiveButton("Bagi ke kantong", (d, w) -> {
                    long gaji = Ui.parseNominal(et.getText());
                    repo.gajiMasuk(gaji, err -> {
                        if (err != null) {
                            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
                        } else {
                            prefs.setGajiTerakhir(gaji);
                            Toast.makeText(this, "Gaji " + Ui.rupiah(gaji) + " sudah dibagi ke kantong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private Spinner kantongSpinner() {
        Spinner sp = new Spinner(this);
        List<String> names = new ArrayList<>();
        for (Pocket p : current) names.add(p.name + " (" + Ui.rupiah(p.balance) + ")");
        sp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names));
        return sp;
    }

    private void dialogPengeluaran() {
        if (current.isEmpty()) return;
        LinearLayout col = Ui.column(this);
        Spinner sp = kantongSpinner();
        EditText etNominal = Ui.field(this, "Nominal (Rp)", true);
        EditText etCatatan = Ui.field(this, "Catatan (opsional)", false);
        col.addView(sp);
        col.addView(etNominal);
        col.addView(etCatatan);

        new AlertDialog.Builder(this)
                .setTitle("Pengeluaran dari kantong")
                .setView(col)
                .setPositiveButton("Simpan", (d, w) -> {
                    Pocket p = current.get(sp.getSelectedItemPosition());
                    repo.pengeluaran(p.id, Ui.parseNominal(etNominal.getText()),
                            etCatatan.getText().toString(), this::toastError);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void dialogTransfer() {
        if (current.size() < 2) {
            Toast.makeText(this, "Butuh minimal 2 kantong", Toast.LENGTH_SHORT).show();
            return;
        }
        LinearLayout col = Ui.column(this);
        TextView l1 = new TextView(this);
        l1.setText("Dari kantong");
        Spinner dari = kantongSpinner();
        TextView l2 = new TextView(this);
        l2.setText("Ke kantong");
        Spinner ke = kantongSpinner();
        ke.setSelection(1);
        EditText etNominal = Ui.field(this, "Nominal (Rp)", true);
        col.addView(l1);
        col.addView(dari);
        col.addView(l2);
        col.addView(ke);
        col.addView(etNominal);

        new AlertDialog.Builder(this)
                .setTitle("Pindah antar kantong")
                .setView(col)
                .setPositiveButton("Pindahkan", (d, w) -> {
                    Pocket a = current.get(dari.getSelectedItemPosition());
                    Pocket b = current.get(ke.getSelectedItemPosition());
                    repo.transfer(a.id, b.id, Ui.parseNominal(etNominal.getText()), this::toastError);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void toastError(String error) {
        if (error != null) Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    // ---------- menu ----------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_riwayat) {
            startActivity(new Intent(this, HistoryActivity.class));
            return true;
        } else if (id == R.id.menu_pengaturan) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.kantongku.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kantongku.app.data.Pocket;
import com.kantongku.app.data.Repository;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private PrefsManager prefs;
    private Repository repo;
    private TextView tvTotalPersen;
    private final List<Pocket> current = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = new PrefsManager(this);
        repo = new Repository(this);

        EditText etNama = findViewById(R.id.etNama);
        SwitchMaterial swTema = findViewById(R.id.swTema);
        tvTotalPersen = findViewById(R.id.tvTotalPersen);

        etNama.setText(prefs.getNama());
        swTema.setChecked(prefs.isTemaGelap());
        // listener dipasang setelah setChecked agar tidak terpicu saat inisialisasi
        swTema.setOnCheckedChangeListener((btn, checked) -> {
            prefs.setTemaGelap(checked);
            AppCompatDelegate.setDefaultNightMode(checked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
        });

        findViewById(R.id.btnSimpan).setOnClickListener(v -> {
            prefs.setNama(etNama.getText().toString().trim());
            Toast.makeText(this, "Pengaturan tersimpan", Toast.LENGTH_SHORT).show();
        });

        RecyclerView rv = findViewById(R.id.rvKantongPengaturan);
        PocketAdapter adapter = new PocketAdapter(this::dialogKantong);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        repo.pockets().observe(this, list -> {
            current.clear();
            if (list != null) current.addAll(list);
            adapter.submit(list);
            int total = 0;
            for (Pocket p : current) total += p.percent;
            tvTotalPersen.setText("Total: " + total + "%" + (total == 100 ? " ✓" : " (harus 100%)"));
        });

        findViewById(R.id.btnTambah).setOnClickListener(v -> dialogKantong(null));
    }

    private void dialogKantong(Pocket existing) {
        LinearLayout col = Ui.column(this);
        EditText etName = Ui.field(this, "Nama kantong", false);
        EditText etPersen = Ui.field(this, "Persentase (0-100)", true);
        col.addView(etName);
        col.addView(etPersen);

        if (existing != null) {
            etName.setText(existing.name);
            etPersen.setText(String.valueOf(existing.percent));
        } else {
            int total = 0;
            for (Pocket p : current) total += p.percent;
            etPersen.setText(String.valueOf(Math.max(0, 100 - total)));
        }

        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Tambah kantong" : "Ubah kantong")
                .setView(col)
                .setPositiveButton("Simpan", (d, w) -> {
                    int persen = (int) Math.min(Ui.parseNominal(etPersen.getText()), 1000);
                    repo.simpanKantong(existing == null ? 0 : existing.id,
                            etName.getText().toString(), persen, this::toastError);
                })
                .setNegativeButton("Batal", null);
        if (existing != null) {
            b.setNeutralButton("Hapus", (d, w) -> repo.hapusKantong(existing.id, this::toastError));
        }
        b.show();
    }

    private void toastError(String error) {
        if (error != null) Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

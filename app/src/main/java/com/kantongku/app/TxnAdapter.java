package com.kantongku.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kantongku.app.data.Txn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TxnAdapter extends RecyclerView.Adapter<TxnAdapter.VH> {

    private final List<Txn> items = new ArrayList<>();
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id", "ID"));

    public void submit(List<Txn> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_txn, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Txn t = items.get(position);
        h.catatan.setText(t.note);
        h.info.setText(t.pocketName + " • " + fmt.format(new Date(t.createdAt)));
        boolean masuk = t.isMasuk();
        h.nominal.setText((masuk ? "+" : "-") + Ui.rupiah(t.amount));
        h.nominal.setTextColor(ContextCompat.getColor(h.itemView.getContext(),
                masuk ? R.color.masuk : R.color.keluar));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView catatan, info, nominal;

        VH(View v) {
            super(v);
            catatan = v.findViewById(R.id.tvCatatan);
            info = v.findViewById(R.id.tvInfo);
            nominal = v.findViewById(R.id.tvNominal);
        }
    }
}

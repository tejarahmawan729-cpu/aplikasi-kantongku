package com.kantongku.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kantongku.app.data.Pocket;

import java.util.ArrayList;
import java.util.List;

public class PocketAdapter extends RecyclerView.Adapter<PocketAdapter.VH> {

    public interface OnClick {
        void onClick(Pocket pocket);
    }

    private final List<Pocket> items = new ArrayList<>();
    private final OnClick onClick;

    public PocketAdapter(OnClick onClick) {
        this.onClick = onClick;
    }

    public void submit(List<Pocket> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pocket, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Pocket p = items.get(position);
        h.nama.setText(p.name);
        h.persen.setText(p.percent + "% dari gaji");
        h.saldo.setText(Ui.rupiah(p.balance));
        h.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView nama, persen, saldo;

        VH(View v) {
            super(v);
            nama = v.findViewById(R.id.tvNama);
            persen = v.findViewById(R.id.tvPersen);
            saldo = v.findViewById(R.id.tvSaldo);
        }
    }
}

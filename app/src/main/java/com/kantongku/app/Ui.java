package com.kantongku.app;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.text.NumberFormat;
import java.util.Locale;

/** Helper kecil untuk format rupiah, parsing nominal, dan membuat isi dialog. */
public final class Ui {
    private Ui() {
    }

    public static String rupiah(long v) {
        NumberFormat nf = NumberFormat.getIntegerInstance(new Locale("id", "ID"));
        return (v < 0 ? "-Rp " : "Rp ") + nf.format(Math.abs(v));
    }

    /** Ambil angka saja ("1.500.000" -> 1500000). Mengembalikan 0 bila kosong/tidak valid. */
    public static long parseNominal(CharSequence s) {
        String digits = s == null ? "" : s.toString().replaceAll("[^0-9]", "");
        if (digits.isEmpty() || digits.length() > 15) return 0;
        return Long.parseLong(digits);
    }

    public static int dp(Context c, int v) {
        return Math.round(v * c.getResources().getDisplayMetrics().density);
    }

    public static LinearLayout column(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        int p = dp(c, 20);
        l.setPadding(p, dp(c, 8), p, 0);
        return l;
    }

    public static EditText field(Context c, String hint, boolean angka) {
        EditText e = new EditText(c);
        e.setHint(hint);
        e.setInputType(angka ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT);
        e.setFilters(new InputFilter[]{new InputFilter.LengthFilter(angka ? 15 : 40)});
        return e;
    }
}

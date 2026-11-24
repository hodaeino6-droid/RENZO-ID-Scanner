package com.renzo.idscanner;

import android.content.Context;
import android.content.SharedPreferences;

/** Local-only storage (app-private SharedPreferences), one record per package. */
final class RecordStore {
    private final SharedPreferences prefs;

    RecordStore(Context c) {
        prefs = c.getApplicationContext().getSharedPreferences("records", Context.MODE_PRIVATE);
    }

    boolean save(String pkg, String record) {
        if (pkg == null || pkg.isEmpty() || !RecordFormat.isValid(record)) return false;
        return prefs.edit().putString(pkg, record).commit();
    }

    String get(String pkg) {
        return prefs.getString(pkg, null);
    }
}

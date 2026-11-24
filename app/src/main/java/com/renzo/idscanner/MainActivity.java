package com.renzo.idscanner;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final int BG = Color.parseColor("#121212");

    private static final class Item {
        String label, pkg; Drawable icon;
    }

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final List<Item> items = new ArrayList<>();
    private RecordStore store;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        store = new RecordStore(this);
        float d = getResources().getDisplayMetrics().density;

        GridView grid = new GridView(this);
        grid.setBackgroundColor(BG);
        grid.setNumColumns(4);
        grid.setVerticalSpacing((int) (12 * d));
        grid.setPadding((int) (8 * d), (int) (24 * d), (int) (8 * d), (int) (8 * d));
        grid.setClipToPadding(false);
        grid.setSelector(android.R.color.transparent);
        adapter = new Adapter(d);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener((p, v, pos, id) -> onTap(items.get(pos)));
        setContentView(grid);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        io.execute(this::loadApps);
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        Intent q = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> ris = pm.queryIntentActivities(q, 0);
        List<Item> out = new ArrayList<>();
        java.util.HashSet<String> seen = new java.util.HashSet<>();
        for (ResolveInfo ri : ris) {
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(getPackageName()) || !seen.add(pkg)) continue;
            Item it = new Item();
            it.pkg = pkg;
            it.label = String.valueOf(ri.loadLabel(pm));
            it.icon = ri.loadIcon(pm);
            out.add(it);
        }
        final Collator c = Collator.getInstance();
        Collections.sort(out, (a, z) -> c.compare(a.label, z.label));
        runOnUiThread(() -> { items.clear(); items.addAll(out); adapter.notifyDataSetChanged(); });
    }

    private void onTap(Item it) {
        String rec = store.get(it.pkg);
        if (rec == null) {
            Toast.makeText(this, "Diagnostic information unavailable for this app", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", rec);
        if (Build.VERSION.SDK_INT >= 33) {
            PersistableBundle pb = new PersistableBundle();
            pb.putBoolean("android.content.extra.IS_SENSITIVE", true);
            clip.getDescription().setExtras(pb);
        }
        cm.setPrimaryClip(clip);
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onResume() { super.onResume(); adapter.notifyDataSetChanged(); }
    @Override protected void onDestroy() { super.onDestroy(); io.shutdownNow(); }

    private final class Adapter extends BaseAdapter {
        private final float d;
        Adapter(float d) { this.d = d; }
        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int i) { return items.get(i); }
        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int pos, View v, ViewGroup parent) {
            ImageView img;
            if (v == null) {
                img = new ImageView(MainActivity.this);
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int pad = (int) (6 * d);
                img.setPadding(pad, pad, pad, pad);
                img.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, (int) (64 * d)));
                v = img;
            } else {
                img = (ImageView) v;
            }
            Item it = items.get(pos);
            img.setImageDrawable(it.icon);
            img.setContentDescription(it.label);   // accessibility only, not visible
            // Dim apps that have not supplied a record yet (no extra controls needed).
            img.setAlpha(store.get(it.pkg) == null ? 0.45f : 1f);
            return v;
        }
    }
}

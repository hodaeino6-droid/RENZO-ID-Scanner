package com.renzo.sample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Stand-in for an app you own. It exports its OWN diagnostic data to RENZO ID Scanner.
 *
 * In a real app, FIELD 1 comes from the official SDK call
 *     AppsFlyerLib.getInstance().getAppsFlyerUID(context)
 * Fields 2 and 3 are whatever your app defines. The values below are TEST FIXTURES only.
 */
public class MainActivity extends Activity {
    static final String TEST_UUID   = "c737bcbb-995e-4d9a-b077-308734ff6a98"; // fixture
    static final String TEST_SECOND = "1793979555295-5083449865490225941";   // fixture
    static final String TEST_HEX    = "d378b55b4b36c5db";                     // fixture

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        String ua = WebSettings.getDefaultUserAgent(this); // the real WebView UA of this device
        final String record = TEST_UUID + "|" + TEST_SECOND + "|" + TEST_HEX + "|" + ua;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 64, 32, 32);
        TextView tv = new TextView(this);
        tv.setText(record);
        tv.setTextIsSelectable(true);
        Button send = new Button(this);
        send.setText("Export to RENZO ID Scanner");
        send.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, record)
                    .setPackage("com.renzo.idscanner");
            try {
                startActivityForResult(i, 1);   // gives the scanner a verified caller package
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "RENZO ID Scanner is not installed", Toast.LENGTH_LONG).show();
            }
        });
        root.addView(send);
        root.addView(tv);
        setContentView(root);
    }
}

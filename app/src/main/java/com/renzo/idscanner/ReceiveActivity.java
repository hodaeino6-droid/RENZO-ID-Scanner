package com.renzo.idscanner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/** Accepts an ACTION_SEND text/plain record and stores it under the sender's package. */
public class ReceiveActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        Intent in = getIntent();
        String text = null;
        CharSequence cs = in == null ? null : in.getCharSequenceExtra(Intent.EXTRA_TEXT);
        if (cs != null) text = cs.toString();

        // Verified caller (startActivityForResult) first, then the launch referrer.
        String pkg = getCallingPackage();
        if (pkg == null) {
            Uri r = getReferrer();
            if (r != null && "android-app".equals(r.getScheme())) pkg = r.getAuthority();
        }
        if (pkg != null && pkg.equals(getPackageName())) pkg = null;

        if (pkg == null) {
            Toast.makeText(this, "Rejected: sender app could not be identified", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
        } else if (new RecordStore(this).save(pkg, text)) {
            Toast.makeText(this, "Saved record for " + pkg, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        } else {
            Toast.makeText(this, "Rejected: not a valid 4-field record", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}

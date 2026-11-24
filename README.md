# RENZO ID Scanner

Native Java, zero dependencies. Two modules: `app` (the scanner) and `sample` (a test app that exports its own record).

## Build (needs Android Studio or JDK 17 + Android SDK 34)
1. Open this folder in Android Studio (it regenerates the Gradle wrapper), or run `gradle wrapper --gradle-version 8.7`.
2. `./make-keystore.sh`, then copy `keystore.properties.example` to `keystore.properties` and fill in passwords.
3. `./gradlew :app:assembleRelease`
   Output: `app/build/outputs/apk/release/app-release.apk` (R8 + resource shrinking on, signed with your key).
4. Exact size: `stat -c %s app/build/outputs/apk/release/app-release.apk`
5. Test app: `./gradlew :sample:assembleDebug`

## Test workflow
1. Install both APKs: `adb install app-release.apk` and `adb install sample/build/outputs/apk/debug/sample-debug.apk`
2. Open Sample Exporter, tap "Export to RENZO ID Scanner" -> toast "Saved record for com.renzo.sample".
3. Open RENZO ID Scanner: Sample Exporter's icon is full brightness (others dimmed). Tap it -> "Copied".
4. Paste anywhere; it must read `UUID|SECOND_ID|HEX_ID|USER_AGENT` with exactly 3 pipes.
5. Tap a dimmed app -> "Diagnostic information unavailable for <app>".
6. Negative test: records with a newline, quotes, wrong pipe count or malformed UUID/hex are rejected.

Validation logic test (no Android needed): see `tools/test/RecordFormatTest.java`.

## Integrating your own authorized app
Send `ACTION_SEND`, `text/plain`, `EXTRA_TEXT` = the exact 4-field string, with `setPackage("com.renzo.idscanner")`
and `startActivityForResult` so the sender package is verified. UUID: `AppsFlyerLib.getInstance().getAppsFlyerUID(ctx)`.
User-Agent: `WebSettings.getDefaultUserAgent(ctx)` or your WebView's `getSettings().getUserAgentString()`.

## Get the APKs: push to GitHub
1. Create an empty GitHub repo, then `git init && git add . && git commit -m init && git remote add origin <url> && git push -u origin main`.
2. The "Build APKs" workflow runs on push (or Actions -> Build APKs -> Run workflow).
3. When it finishes, download the three artifacts: RENZO-ID-Scanner-debug, RENZO-ID-Scanner-release, Sample-Exporter-debug.
   Exact byte/MB sizes are in the job log and the run summary. The run fails if any APK is missing.
The release APK is signed with the Android DEBUG key (test build) unless you add your own keystore.properties.

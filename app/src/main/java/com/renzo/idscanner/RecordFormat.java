package com.renzo.idscanner;

import java.util.regex.Pattern;

/** Strict check of UUID|SECOND_ID|HEX_ID|USER_AGENT. Never modifies the input. */
final class RecordFormat {
    private static final Pattern UUID = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    private static final Pattern HEX = Pattern.compile("[0-9a-fA-F]+");
    private static final int MAX_LEN = 4096;

    private RecordFormat() {}

    static boolean isValid(String s) {
        if (s == null || s.isEmpty() || s.length() > MAX_LEN) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < 0x20 || c == '"') return false;      // no line breaks / control chars / quotes
        }
        if (!s.equals(s.trim())) return false;            // no leading/trailing spaces
        String[] p = s.split("\\|", -1);
        if (p.length != 4) return false;                  // exactly three pipes
        for (String part : p) {
            if (part.isEmpty() || !part.equals(part.trim())) return false;
        }
        if (!UUID.matcher(p[0]).matches()) return false;
        if (p[1].indexOf(' ') >= 0) return false;
        if (!HEX.matcher(p[2]).matches()) return false;
        return true;
    }
}

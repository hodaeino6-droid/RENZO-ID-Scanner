package com.renzo.idscanner;

public class RecordFormatTest {
    static int fails = 0;
    static void check(String name, boolean expect, String s) {
        boolean got = RecordFormat.isValid(s);
        System.out.println((got == expect ? "PASS " : "FAIL ") + name);
        if (got != expect) fails++;
    }
    public static void main(String[] a) {
        String ua = "Mozilla/5.0 (Linux; Android 13; Pixel 6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
        String ok = "c737bcbb-995e-4d9a-b077-308734ff6a98|1793979555295-5083449865490225941|d378b55b4b36c5db|" + ua;
        check("spec example is valid", true, ok);
        check("trailing newline rejected", false, ok + "\n");
        check("trailing space rejected", false, ok + " ");
        check("only 2 pipes", false, "c737bcbb-995e-4d9a-b077-308734ff6a98|1793979555295-5083449865490225941|d378b55b4b36c5db");
        check("4 pipes (pipe in UA)", false, ok + "|x");
        check("bad uuid", false, "zzzz|1793979555295-5083449865490225941|d378b55b4b36c5db|" + ua);
        check("non-hex third field", false, "c737bcbb-995e-4d9a-b077-308734ff6a98|1793979555295-5083449865490225941|ghij|" + ua);
        check("empty UA", false, "c737bcbb-995e-4d9a-b077-308734ff6a98|1793979555295-5083449865490225941|d378b55b4b36c5db|");
        check("labels/quotes rejected", false, "\"" + ok + "\"");
        check("null", false, null);
        System.out.println(fails == 0 ? "ALL PASSED" : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}

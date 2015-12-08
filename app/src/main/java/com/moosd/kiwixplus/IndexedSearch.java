package com.moosd.kiwixplus;

/**
 * Created by souradip on 08/12/15.
 */
public class IndexedSearch {
    static {
        System.loadLibrary("idxsearch");
    }

    public static native String query(String db, String query);
}

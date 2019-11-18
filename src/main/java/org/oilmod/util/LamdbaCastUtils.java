package org.oilmod.util;

public class LamdbaCastUtils {
    public static <To, From> To cast(From from) {
        //noinspection unchecked
        return (To) from;
    }
}

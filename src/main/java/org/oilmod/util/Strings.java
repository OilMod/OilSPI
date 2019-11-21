package org.oilmod.util;

import java.util.function.Function;

public class Strings {

    public static <T> String concatArray(T... values) {
        return concatArray(Object::toString, values);
    }

    public static <T> String concatArray(Function<T, String> toString, T... values) {
        StringBuilder sb = new StringBuilder("(");
        boolean isFirst = true;
        for (T value:values) {
            if (!isFirst) {
                sb.append(", ");
            }
            isFirst = false;
            sb.append(value==null?"null":toString.apply(value));
        }
        sb.append(')');
        return sb.toString();
    }


    public static String concatClassesSimple(Class... classes) {
        return concatArray(Class::getSimpleName, classes);
    }
}

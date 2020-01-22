package org.oilmod.util;

import java.util.function.Function;

public class Strings {

    public static <T> String concatArray(T... values) {
        return concatArray(Object::toString, values);
    }

    @SafeVarargs
    public static <T> String concatArray(Function<T, CharSequence> toString, T... values) {
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


    public static String concatClassesSimple(Class<?>... classes) {
        return concatArray(Strings::simpleName, classes);
    }

    public static CharSequence simpleName(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        appendClass(sb, clazz);
        return sb;
    }

    private static void appendClass(StringBuilder sb, Class<?> clazz) {
        if (clazz.getEnclosingClass() != null) {
            if (clazz.getDeclaringClass() == null) {
                if (clazz.getEnclosingMethod() != null) {
                    appendMethod(sb, clazz);
                } else {
                    appendCtor(sb, clazz);
                }
            } else {
                appendClass(sb, clazz.getDeclaringClass());
            }
            sb.append('$');
        }
        sb.append(clazz.getSimpleName());

    }

    private static void appendMethod(StringBuilder sb, Class<?> clazz) {
        appendClass(sb, clazz.getEnclosingClass());
        sb.append(".");
        sb.append(clazz.getEnclosingMethod().getName());
        sb.append("()");

    }

    private static void appendCtor(StringBuilder sb, Class<?> clazz) {
        appendClass(sb, clazz.getEnclosingClass());
        sb.append("<ctor>");
    }
}

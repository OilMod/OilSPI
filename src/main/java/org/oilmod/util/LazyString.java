package org.oilmod.util;

public class LazyString {
    public static <T> Object lazy(Supplier<T> s) {
        return new LazyToString(s);
    }

    @FunctionalInterface
    public interface Supplier<T>{
        T get();
    }

    private static class LazyToString{
        private final Supplier<?> supp;

        public LazyToString(Supplier<?> supp) {
            this.supp = supp;
        }

        @Override
        public String toString() {
            return supp.get().toString();
        }
    }
}

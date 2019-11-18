package org.oilmod.spi.test;

import org.oilmod.spi.MPILoader;

public class Main {
    public static void main(String... args) {
        MPILoader.init();
        MPILoader.commitDependency("Message1");
        MPILoader.commitDependency("Message2");
    }
}

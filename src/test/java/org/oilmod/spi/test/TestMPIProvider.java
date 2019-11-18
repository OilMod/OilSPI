package org.oilmod.spi.test;

import org.oilmod.spi.dependencies.DependencyPipe;

public class TestMPIProvider extends TestMPI.TestHelper<TestMPIProvider> {

    @Override
    public void addDependencies(DependencyPipe p) {
        p.add(TestMPI.class);
        p.add(TestMPIProvider2.class, System.out::println);
    }


}

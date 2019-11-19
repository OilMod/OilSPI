package org.oilmod.spi.test;

import org.oilmod.spi.dependencies.DependencyPipe;

public class TestMPIProvider2 extends TestMPI2.TestHelper<TestMPIProvider2> {


    @Override
    public void addDependencies(DependencyPipe p) {
        //p.add(TestMPIProvider.class, System.out::println);
    }
}

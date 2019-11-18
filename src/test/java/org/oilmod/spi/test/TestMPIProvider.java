package org.oilmod.spi.test;

public class TestMPIProvider extends TestMPI.TestHelper<TestMPIProvider> {

    @Override
    public Class<? extends TestMPIProvider> getImplementationClass() {
        return TestMPIProvider.class;
    }


}

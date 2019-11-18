package org.oilmod.spi.test;

import org.oilmod.spi.mpi.ModdingPIServiceBase;
import org.oilmod.spi.provider.IMPIImplementationProvider;
import org.oilmod.spi.mpi.IModdingPIService;

import static org.oilmod.util.LamdbaCastUtils.cast;
import static org.oilmod.util.LamdbaExceptionUtils.uncheck;

public class TestMPI implements ModdingPIServiceBase<TestMPI, TestMPI.TestHelper<?>> {

    @Override
    public Class<TestMPI> getMPIClass() {
        return TestMPI.class;
    }

    public static class TestHelper<Impl extends TestHelper<Impl>> implements IMPIImplementationProvider<TestMPI, TestHelper<?>, Impl> {

        @Override
        public Class<TestMPI> getMPIClass() {
            return TestMPI.class;
        }

        @Override
        public Class<TestHelper<?>> getImplementationBaseClass() {
            return cast(TestHelper.class);
        }
    }
}

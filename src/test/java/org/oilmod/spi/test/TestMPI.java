package org.oilmod.spi.test;

import org.oilmod.spi.dependencies.DependencyPipe;
import org.oilmod.spi.dependencies.IDependency;
import org.oilmod.spi.mpi.ModdingPIServiceBase;
import org.oilmod.spi.mpi.SingleMPI;
import org.oilmod.spi.provider.IMPIImplementationProvider;
import org.oilmod.spi.mpi.IModdingPIService;
import org.oilmod.spi.provider.ImplementationBase;

import static org.oilmod.util.LamdbaCastUtils.cast;
import static org.oilmod.util.LamdbaExceptionUtils.uncheck;

public class TestMPI extends SingleMPI<TestMPI, TestMPI.TestHelper<?>> {
    public TestMPI() {
        super(TestMPI.class, cast(TestHelper.class));
    }

    @Override
    public void addDependencies(DependencyPipe p) {
        p.add(TestMPIProvider2.class);
        p.add(String.class, this::stringMessageHandler, false, false);
        p.add(String.class, this::stringMessageHandler, false, false);
    }

    private void stringMessageHandler(String s) {
        System.out.println("TestMPI's StringMessageHandler received:" + s);
    }

    public abstract static class TestHelper<Impl extends TestHelper<Impl>> extends ImplementationBase<TestMPI, TestHelper<?>, Impl> {
        public TestHelper() {
            super(TestMPI.class, cast(TestHelper.class));
        }
    }
}

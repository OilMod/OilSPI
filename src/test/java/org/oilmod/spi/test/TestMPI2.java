package org.oilmod.spi.test;

import org.oilmod.spi.dependencies.DependencyPipe;
import org.oilmod.spi.dependencies.IDependency;
import org.oilmod.spi.mpi.SingleMPI;
import org.oilmod.spi.provider.IMPIImplementationProvider;
import org.oilmod.spi.provider.ImplementationBase;

import static org.oilmod.util.LamdbaCastUtils.cast;

public class TestMPI2 extends SingleMPI<TestMPI2, TestMPI2.TestHelper<?>> {

    @Override
    public void addDependencies(DependencyPipe p) {
        p.add(TestMPI.class, TestMPIProvider2.class);
    }

    public abstract static class TestHelper<Impl extends TestMPI2.TestHelper<Impl>> extends ImplementationBase<TestMPI2, TestMPI2.TestHelper<?>, Impl> {
    }
}

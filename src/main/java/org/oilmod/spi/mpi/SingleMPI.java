package org.oilmod.spi.mpi;

import org.oilmod.spi.dependencies.IDependency;
import org.oilmod.spi.provider.IMPIImplementationProvider;

public abstract class SingleMPI<MPI extends SingleMPI<MPI, Provider>, Provider extends IMPIImplementationProvider<MPI, Provider, ? extends Provider>> extends ModdingPIServiceBase<MPI, Provider> {
    protected SingleMPI(Class<MPI> mpiClass, Class<Provider> providerClass) {
        super(mpiClass, providerClass);
    }

    @Override
    public boolean acceptMultiple() {
        return false;
    }

}

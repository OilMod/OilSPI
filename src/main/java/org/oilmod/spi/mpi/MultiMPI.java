package org.oilmod.spi.mpi;

import org.oilmod.spi.provider.IMPIImplementationProvider;

public abstract class MultiMPI<MPI extends MultiMPI<MPI, Provider>, Provider extends IMPIImplementationProvider<MPI, Provider, ? extends Provider>> extends ModdingPIServiceBase<MPI, Provider> {
    protected MultiMPI(Class<MPI> mpiClass, Class<Provider> providerClass) {
        super(mpiClass, providerClass);
    }

    @Override
    public boolean acceptMultiple() {
        return true;
    }

}

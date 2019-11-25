package org.oilmod.spi.mpi;

import org.oilmod.spi.dependencies.IDependency;
import org.oilmod.spi.provider.IMPIImplementationProvider;

public abstract class SingleMPI<MPI extends SingleMPI<MPI, Provider>, Provider extends IMPIImplementationProvider<MPI, Provider, ? extends Provider>> extends ModdingPIServiceBase<MPI, Provider> {
    private Provider provider;

    public SingleMPI(Class<MPI> mpiClass, Class<Provider> providerClass) {
        super(mpiClass, providerClass);
    }
    public SingleMPI() {}

    @Override
    public boolean acceptMultiple() {
        return false;
    }

    @Override
    public final void addProvider(Provider provider) {
        super.addProvider(provider);
        this.provider = provider;
        onSetProvider(provider);
    }

    public Provider getProvider() {
        return provider;
    }

    protected void onSetProvider(Provider provider) {}
}

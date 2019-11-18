package org.oilmod.spi;

import org.oilmod.spi.mpi.IModdingPIService;
import org.oilmod.spi.provider.IMPIImplementationProvider;

public interface IMPIClassGetter<MPI extends IModdingPIService<MPI, Provider>, Provider extends IMPIImplementationProvider<MPI, Provider, ? extends Provider>> {
    Class<MPI> getMPIClass();
    Class<Provider> getProviderClass();
}

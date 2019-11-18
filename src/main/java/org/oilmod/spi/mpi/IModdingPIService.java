package org.oilmod.spi.mpi;

import org.oilmod.spi.IMPIClassGetter;
import org.oilmod.spi.dependencies.IDependent;
import org.oilmod.spi.provider.IMPIImplementationProvider;
import org.oilmod.spi.dependencies.IDependency;

import java.util.Set;

public interface IModdingPIService<MPI extends IModdingPIService<MPI, Provider>, Provider extends IMPIImplementationProvider<MPI, Provider, ? extends Provider>> extends IMPIClassGetter<MPI, Provider>, IDependent {
    boolean acceptMultiple();
    void addProvider(Provider provider);
    Set<Provider> getAllProviders();
    default void setup(){}

    @Override
    default Class[] getDependentIdentifierClasses() {
        return new Class[]{getMPIClass()};
    }
}

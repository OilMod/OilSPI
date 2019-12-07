package org.oilmod.spi.mpi;

import org.apache.commons.lang3.NotImplementedException;
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

    /**
     * if true createDefaultProvider() is called when no provider is provided at all!
     * @return
     */
    default boolean hasDefaultProvider() {return false;}
    default Provider createDefaultProvider() {
        if (hasDefaultProvider()) {
            throw new NotImplementedException("The MPI signals that is has a default provider but doesnt actually provide one!");
        } else {
            throw new NotImplementedException("The MPI does not have an default impl! This method should not be called by anything, but the MPILoader");
        }
    }

    @Override
    default Class[] getDependentIdentifierClasses() {
        return new Class[]{getMPIClass()};
    }
}

package org.oilmod.spi.mpi;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.Validate;
import org.oilmod.spi.provider.IMPIImplementationProvider;
import org.oilmod.spi.dependencies.IDependency;

import java.util.Collections;
import java.util.Set;

public abstract class ModdingPIServiceBase<MPI extends ModdingPIServiceBase<MPI, Provider>, Provider extends IMPIImplementationProvider<MPI, Provider, ? extends Provider>> implements IModdingPIService<MPI, Provider> {
    private final Set<Provider> providers = new ObjectOpenHashSet<>();


    @Override
    public void addProvider(Provider provider) {
        Validate.isTrue(acceptMultiple() || providers.size() == 0, "Cannot set more than 1 provider! %s & %s ", providers.iterator().next(), provider);
        providers.add(provider);
    }

    @Override
    public Set<Provider> getAllProviders() {
        return Collections.unmodifiableSet(providers);
    }

    @Override
    public void setDependencies() {

    }

    @Override
    public Set<IDependency> getAllDependencies() {
        return null;
    }
}

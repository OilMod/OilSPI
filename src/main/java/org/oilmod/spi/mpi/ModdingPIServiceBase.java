package org.oilmod.spi.mpi;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.Validate;
import org.oilmod.spi.MPILoader;
import org.oilmod.spi.provider.IMPIImplementationProvider;
import org.oilmod.spi.dependencies.IDependency;
import org.oilmod.spi.provider.ImplementationBase;
import org.oilmod.util.Strings;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.Set;

import static org.oilmod.util.LazyString.lazy;
import static org.oilmod.util.ReflectionUtils.resolveGenericSuperInterface;

public abstract class ModdingPIServiceBase<MPI extends ModdingPIServiceBase<MPI, Provider>, Provider extends IMPIImplementationProvider<MPI, Provider, ? extends Provider>> implements IModdingPIService<MPI, Provider> {
    private final Set<Provider> providers = new ObjectOpenHashSet<>();
    private final Class<MPI> mpiClass;
    private final Class<Provider> providerClass;

    protected ModdingPIServiceBase(Class<MPI> mpiClass, Class<Provider> providerClass) {
        this.mpiClass = mpiClass;
        this.providerClass = providerClass;
    }

    @SuppressWarnings("unchecked")
    protected ModdingPIServiceBase() {
        Class<?>[] generics = resolveGenericSuperInterface(getClass(), IModdingPIService.class);
        this.mpiClass = (Class<MPI>) generics[0];
        this.providerClass = (Class<Provider>) generics[1];

        Class<?>[] genericsImpl = resolveGenericSuperInterface(providerClass, IMPIImplementationProvider.class);
        MPILoader.validateGenerics(generics, genericsImpl, true);
    }

    @Override
    public void addProvider(Provider provider) {
        Validate.isTrue(acceptMultiple() || providers.size() == 0, "Cannot set more than 1 provider for API %s! Got %s & %s ", getMPIClass().getSimpleName() , lazy(()->providers.iterator().next()), provider);
        providers.add(provider);
    }

    @Override
    public Set<Provider> getAllProviders() {
        return Collections.unmodifiableSet(providers);
    }


    @Override
    public Class<Provider> getProviderClass() {
        return providerClass;
    }

    @Override
    public Class<MPI> getMPIClass() {
        return mpiClass;
    }

}

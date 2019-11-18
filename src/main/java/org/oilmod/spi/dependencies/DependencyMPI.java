package org.oilmod.spi.dependencies;

import org.oilmod.spi.IMPIClassGetter;

import java.util.function.Consumer;

public class DependencyMPI<Dependent extends IDependent & IMPIClassGetter, Dependency> extends DependencySimple<Dependency> {

    public DependencyMPI(Class<Dependency> dependency, Consumer<Dependency> dependencyConsumer, Dependent dependent, boolean allowSubType, boolean singleResolve) {
        super(dependency, dependencyConsumer, dependent, allowSubType, singleResolve);
    }

    public DependencyMPI(Class<Dependency> dependency, Dependent dependent, boolean allowSubType, boolean singleResolve) {
        super(dependency, dependent, allowSubType, singleResolve);
    }
}

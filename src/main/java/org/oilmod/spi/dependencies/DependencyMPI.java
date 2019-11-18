package org.oilmod.spi.dependencies;

import org.oilmod.spi.IMPIClassGetter;

import java.util.function.Consumer;

public class DependencyMPI<Dependent extends IDependent & IMPIClassGetter, Dependency> extends DependencySimple<Dependency> {
    public DependencyMPI(Class<org.oilmod.spi.dependencies.Dependent> dependentClass, org.oilmod.spi.dependencies.Dependent dependent, Class<Dependency> dependency, boolean allowSubType, Consumer<Dependency> dependencyConsumer) {
        super(dependent, dependency, allowSubType, dependencyConsumer);
    }

    public DependencyMPI(Class<org.oilmod.spi.dependencies.Dependent> dependentClass, org.oilmod.spi.dependencies.Dependent dependent, Class<Dependency> dependency, boolean allowSubType) {
        super(dependent, dependency, allowSubType);
    }
}

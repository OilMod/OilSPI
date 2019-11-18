package org.oilmod.spi.dependencies;

import java.util.function.Consumer;

public class DependencySimple<Dependency> extends DependencyBase<Dependency> {
    private final boolean allowSubType;

    public DependencySimple(Class<Dependency> dependency, Consumer<Dependency> dependencyConsumer, IDependent dependent, boolean allowSubType, boolean singleResolve) {
        super(dependency, dependencyConsumer, dependent, singleResolve);
        this.allowSubType = allowSubType;
    }


    public DependencySimple(Class<Dependency> dependency, IDependent dependent, boolean allowSubType, boolean singleResolve) {
        this(dependency, (d)->{}, dependent, allowSubType, singleResolve);
    }


    @Override
    public boolean checkType(Object candidate) {
        return allowSubType? getDependencyClass().isInstance(candidate) : candidate.getClass().equals(getDependencyClass());
    }

    @Override
    public boolean checkCandidate(Dependency candidate, boolean ready) {
        return true;
    }

}

package org.oilmod.spi.dependencies;

import java.util.function.Consumer;

public class DependencySimple<Dependency> extends DependencyBase<Dependency> {
    private final IDependent dependent;
    private final boolean allowSubType;

    public DependencySimple(IDependent dependent, Class<Dependency> dependency, boolean allowSubType, Consumer<Dependency> dependencyConsumer) {
        super(dependency, dependencyConsumer);
        this.dependent = dependent;
        this.allowSubType = allowSubType;
    }


    public DependencySimple(IDependent dependent, Class<Dependency> dependency, boolean allowSubType) {
        this(dependent, dependency, allowSubType, (d)->{});
    }


    @Override
    public boolean checkType(Object candidate) {
        return allowSubType? getDependencyClass().isInstance(candidate) : candidate.getClass().equals(getDependencyClass());
    }

    @Override
    public boolean checkCandidate(Dependency candidate) {
        return true;
    }

    @Override
    public IDependent getDependent() {
        return dependent;
    }
}

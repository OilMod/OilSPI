package org.oilmod.spi.dependencies;

import java.util.function.Consumer;

public abstract class DependencyBase<Dependency> implements IDependency<Dependency> {
    private final Class<Dependency> dependencyClass;
    private final Consumer<Dependency> dependencyConsumer;
    private boolean isResolved = false;
    private final boolean singleResolve;
    private final IDependent dependent;


    protected DependencyBase(Class<Dependency> dependencyClass, Consumer<Dependency> dependencyConsumer, IDependent dependent, boolean singleResolve) {
        this.dependencyClass = dependencyClass;
        this.dependencyConsumer = dependencyConsumer;
        this.singleResolve = singleResolve;
        this.dependent = dependent;
    }


    @Override
    public boolean accept(Dependency candidate) {
        try {
            if (isResolved && singleResolve) throw new IllegalStateException("Dependency was resolved multiple times");
            dependencyConsumer.accept(candidate);

            return !isResolved;
        } finally {
            isResolved = true;
        }

    }

    @Override
    public Class<Dependency> getDependencyClass() {
        return dependencyClass;
    }

    @Override
    public IDependent getDependent() {
        return dependent;
    }
}

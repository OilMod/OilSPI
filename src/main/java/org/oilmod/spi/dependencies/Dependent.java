package org.oilmod.spi.dependencies;

public class Dependent implements IDependent {
    @Override
    public final <Dependency> void onResolved(IDependency<Dependency> dependencyHolder, Dependency resolvedDependency) {

    }

    @Override
    public void allDepResolved() {

    }

    @Override
    public Class[] getDependentIdentifierClasses() {
        return new Class[0];
    }
}

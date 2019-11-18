package org.oilmod.spi.dependencies;

public interface IDependent {
    <Dependency> void onResolved(IDependency dependencyHolder, Dependency resolvedDependency);
    void allDepResolved();
    Class[] getDependentIdentifierClasses();
}

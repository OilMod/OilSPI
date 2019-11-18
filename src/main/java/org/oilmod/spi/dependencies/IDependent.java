package org.oilmod.spi.dependencies;

public interface IDependent {
    default <Dependency> void onResolved(IDependency<Dependency> dependencyHolder, Dependency resolvedDependency) {}
    default void allDepResolved() {
        System.out.println("all dependencies got resolved! " + getClass().getSimpleName());
    }
    Class[] getDependentIdentifierClasses();
    default void addDependencies(DependencyPipe p) {}
}

package org.oilmod.spi.dependencies;

import static org.oilmod.util.Strings.simpleName;

public interface IDependent {
    default <Dependency> void onResolved(IDependency<Dependency> dependencyHolder, Dependency resolvedDependency) {}
    default void allDepResolved() {
        System.out.println("all dependencies got resolved! " + simpleName(getClass()));
    }
    Class[] getDependentIdentifierClasses();
    default void addDependencies(DependencyPipe p) {}
}

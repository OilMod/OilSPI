package org.oilmod.spi.dependencies;

public class DependentException extends DependencyException {
    private final IDependent dependent;

    public DependentException(String message, IDependent dependent, Exception e) {
        super(message, e);
        this.dependent = dependent;
    }

    public IDependent getDependent() {
        return dependent;
    }
}

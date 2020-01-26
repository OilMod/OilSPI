package org.oilmod.spi.dependencies;

public class DependentInitException extends DependentException {

    public DependentInitException(IDependent dependent, Exception e) {
        super("Exception caught during initialisation of " + dependent.toString(), dependent, e);
    }
}

package org.oilmod.spi.dependencies;

public class DependencyException extends RuntimeException {
    public DependencyException(String message, Throwable e) {
        super(message, e);
    }
}

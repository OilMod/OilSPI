package org.oilmod.spi.dependencies;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public abstract class DependencyBase<Dependency> implements IDependency<Dependency> {
    private WeakReference<DependencyNode> node;
    private final Class<Dependency> dependencyClass;
    private final Consumer<Dependency> dependencyConsumer;


    protected DependencyBase(Class<Dependency> dependencyClass, Consumer<Dependency> dependencyConsumer) {
        this.dependencyClass = dependencyClass;
        this.dependencyConsumer = dependencyConsumer;
    }

    @Override
    public final DependencyBase<Dependency> clone() {
        try {
            //noinspection unchecked
            return (DependencyBase<Dependency>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("wha?",e);
        }
    }

    @Override
    public void setNode(DependencyNode node) {
        if (node == null) {
            this.node.clear();
            this.node = null;
        } else if (getNode() == null) {
            this.node = new WeakReference<>(node);
        } else throw new IllegalStateException("Cannot set more than one node per dependency, use cloneIfNeeded to always get a clean instance!");
    }

    @Override
    public DependencyNode getNode() {
        return node==null?null:node.get();
    }

    @Override
    public Class<Dependency> getDependencyClass() {
        return dependencyClass;
    }

    @Override
    public Consumer<Dependency> getDependencyConsumer() {
        return dependencyConsumer;
    }
}

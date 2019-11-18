package org.oilmod.spi.dependencies;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DependencyPipe {
    private final DependencyGraph graph;
    private IDependent current;

    public DependencyPipe(DependencyGraph graph) {
        this.graph = graph;
    }

    public void run(IDependent dependent) {
        current = dependent;
        current.addDependencies(this);
        current = null;
    }

    public <Dependency> void add(Class<Dependency> clazz, Consumer<Dependency> consumer, boolean allowSubType, boolean singleResolve) {
        graph.add(new DependencySimple<>(clazz, consumer, current, allowSubType, singleResolve));
    }

    public <Dependency> void add(Class<Dependency> clazz, Consumer<Dependency> consumer) {
        graph.add(new DependencySimple<>(clazz, consumer, current, false, true));
    }

    public <Dependency> void add(Class<Dependency> clazz, boolean allowSubType, boolean singleResolve) {
        graph.add(new DependencySimple<>(clazz, current, allowSubType, singleResolve));
    }

    public <Dependency> void add(Class<Dependency> clazz) {
        graph.add(new DependencySimple<>(clazz, current, false, true));
    }

    public void add(Class... clazz) {
        Arrays.stream(clazz).forEach(this::add);
    }

}

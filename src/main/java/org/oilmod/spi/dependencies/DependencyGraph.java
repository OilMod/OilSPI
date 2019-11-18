package org.oilmod.spi.dependencies;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class DependencyGraph {
    private DependencyNode root;
    private Map<Class, DependencyNode> map = new Object2ObjectOpenHashMap<>();
    private Map<Class, Set<DependencyNode>> waiting = new Object2ObjectOpenHashMap<>();

    public void add(IDependency dependency) {
        for (Class clazz:dependency.getDependent().getDependentIdentifierClasses()) {
            DependencyNode node = map.computeIfAbsent(clazz, c->new DependencyNode(DependencyGraph.this, dependent));
            node.add(dependency);
            Set<DependencyNode> waitSet;
            if ((waitSet=waiting.remove(clazz)) != null) {
                waitSet.forEach(node::add); //add children, children were here first so no need to check circle ref
            }
        }
        map.get()


        Arrays.stream(dependency.getDependent().getDependentIdentifierClasses())
                .map(c->)
                .peek(note->note.add(dependency))
                .;


        map.computeIfPresent(dependency.getDependencyClass(), (clazz, node) -> {
            node.
        })
    }

    public void consume(Object o) {
        root.consume(o);
    }
}

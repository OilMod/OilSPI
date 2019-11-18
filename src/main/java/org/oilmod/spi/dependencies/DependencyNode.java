package org.oilmod.spi.dependencies;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Set;
import java.util.stream.Stream;

public class DependencyNode {
    private final Set<IDependency> dependencies = new ObjectOpenHashSet<>();
    private final Set<IDependency> dependenciesResolved = new ObjectOpenHashSet<>();
    private final Set<DependencyNode> children = new ObjectOpenHashSet<>();
    private final DependencyGraph graph;
    private final Dependent dependent;

    public DependencyNode(DependencyGraph graph, Dependent dependent) {
        this.graph = graph;
        this.dependent = dependent;
    }

    public void add(IDependency dependency) {
        dependency = dependency.cloneIfNeeded();
        dependency.setNode(this);
        dependencies.add(dependency);
    }


    private void markResolved(IDependency dependency) {
        dependencies.remove(dependency);
        dependenciesResolved.add(dependency);
    }



    public void add(DependencyNode child) {
        children.add(child);
    }


    private void remove(DependencyNode dependency) {
        children.remove(dependency);
    }

    public Stream<IDependency> traverseTreeDependencies() {
        return traverseTree().flatMap(note->Stream.concat(note.dependencies.stream(), note.dependenciesResolved.stream()));

    }
    public Stream<DependencyNode> traverseTree() {
        return Stream.concat(Stream.of(this), children.stream().flatMap(DependencyNode::traverseTree));
    }

    @SuppressWarnings("unchecked")
    public void consume(Object o) {
        for (IDependency dep:dependencies) {

        }


        traverseTreeDependencies().filter(dep->dep.checkType(o) && dep.checkCandidate(o))
                .forEach(dep-> {
                    boolean first = dep.accept(o);
                    DependencyNode note = dep.getNode();

                    if (first) {
                        note.markResolved(dep);


                        if (note.dependencies.size() == 0) {
                            dep.getDependent().allDepResolved();

                            //lets not deconstruct tree as we still want to use it
                            //if (note.parent != null) {
                            //    note.parent.remove(DependencyNode.this);
                            //}
                            consume(dep.getDependent()); //
                        }
                    }

                });
    }

    public static void consume(Stream<DependencyNode> stream, Object o) {
        stream.forEach(node->node.consume(o));
    }
}

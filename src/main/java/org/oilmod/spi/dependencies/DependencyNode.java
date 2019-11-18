package org.oilmod.spi.dependencies;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Map;
import java.util.Set;

public class DependencyNode {
    private final Set<IDependency> myDeps = new ObjectOpenHashSet<>();
    private final Map<IDependency, DependencyNode> onMeDeps = new Object2ObjectOpenHashMap<>();
    private final DependencyGraph graph;
    private final IDependent dependent;

    public DependencyNode(DependencyGraph graph, IDependent dependent) {
        this.graph = graph;
        this.dependent = dependent;
    }

    public void addMy(IDependency dependency) {

        myDeps.add(dependency);
    }
    public void addOnMe(IDependency dependency, DependencyNode node) {
        if (onMeDeps.containsKey(dependency))throw new IllegalStateException("how lol");
        onMeDeps.put(dependency, node);
    }


    public void consumeOnMe(Object o) {
        for (Map.Entry<IDependency, DependencyNode> entry: onMeDeps.entrySet()) {

            //System.out.println("Doing consumeOnMe from "+ dependent.getClass().getSimpleName() + " to call " + entry.getValue().dependent.getClass().getSimpleName() + " with " + o.getClass().getSimpleName());
            entry.getValue().check(o, entry.getKey());
        }
    }


    public void check(Object o, IDependency dep) {
        //System.out.println("check!");
        //type should already be checked!
        if (!dep.checkCandidate(o, true))return;
        boolean first = dep.accept(o);
        myDeps.remove(dep);
        //System.out.println("removed " + dep.getDependencyClass().getSimpleName() + " from mydeps for " + dependent.getClass().getSimpleName());
        if (first && areDepResolved()) {
            //System.out.println("Calling initDependant for "+ dependent.getClass().getSimpleName());
            initDependant();
        }
    }

    public boolean areDepResolved() {
        return myDeps.size() == 0;
    }



    public void initDependant() {
        dependent.allDepResolved();
        graph.consume(dependent);
    }

}

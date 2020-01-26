package org.oilmod.spi.dependencies;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.oilmod.util.Strings.concatClassesSimple;

public class DependencyNode {
    private final Set<IDependency> myDeps = new ObjectOpenHashSet<>();
    private final Map<IDependency, DependencyNode> onMeDeps = new Object2ObjectOpenHashMap<>();
    private final Set<DependencyNode> children = new ObjectOpenHashSet<>();
    private final DependencyGraph graph;
    private final IDependent dependent;

    public DependencyNode(DependencyGraph graph, IDependent dependent) {
        this.graph = graph;
        this.dependent = dependent;
    }

    public void addMy(IDependency dependency) {
        if (checkSuitable(dependency, dependent))throw new IllegalStateException(String.format("Cannot depend on yourself: %s", concatClassesSimple(dependent.getDependentIdentifierClasses())));

        IDependent circle = checkCircleDep(dependency);
        if (circle != null)throw new IllegalStateException(String.format("Cannot have circle dependency! The two dependents %s and %s are directly or indirectly linked", concatClassesSimple(dependent.getDependentIdentifierClasses()), concatClassesSimple(circle.getDependentIdentifierClasses())));
        myDeps.add(dependency);

    }

    private IDependent checkCircleDep(IDependency dependency) {
        Optional<IDependency> found = myDeps.stream().filter(d -> checkSuitable(dependency, d.getDependent())).findAny();
        if (found.isPresent()) return found.get().getDependent();
        return children.stream().map(c -> c.checkCircleDep(dependency)).filter(Objects::nonNull).findAny().orElse(null);
    }

    private boolean checkSuitable(IDependency dependency, IDependent dependent) {
        return dependency.checkType(dependent) && dependency.checkCandidate(dependent, false);
    }

    public void addOnMe(IDependency dependency, DependencyNode node) {
        if (onMeDeps.containsKey(dependency))throw new IllegalStateException("how lol");
        onMeDeps.put(dependency, node);
        children.add(node);
    }


    public void consumeOnMe(Object o) {
        for (Map.Entry<IDependency, DependencyNode> entry: onMeDeps.entrySet()) {

            //System.out.println("Doing consumeOnMe from "+ dependent.simpleName(getClass()) + " to call " + entry.getValue().dependent.simpleName(getClass()) + " with " + o.simpleName(getClass()));
            entry.getValue().check(o, entry.getKey());
        }
    }


    public void check(Object o, IDependency dep) {
        //System.out.println("check!");
        //type should already be checked!
        if (!dep.checkCandidate(o, true))return;
        boolean first = dep.accept(o);
        myDeps.remove(dep);
        //System.out.println("removed " + dep.simpleName(getDependencyClass()) + " from mydeps for " + dependent.simpleName(getClass()));
        if (first && areDepResolved()) {
            //System.out.println("Calling initDependant for "+ dependent.simpleName(getClass()));
            initDependant();
        }
    }

    public boolean areDepResolved() {
        return myDeps.size() == 0;
    }



    public void initDependant() {
        boolean noException = true;
        try {
            dependent.allDepResolved();
        } catch (RuntimeException e){
            DependentInitException initEx = new DependentInitException(dependent, e);
            graph.exceptions.add(initEx);
            noException = false;
        }
        if (noException) {
            graph._consume(dependent);
        }
    }

}

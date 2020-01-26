package org.oilmod.spi.dependencies;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.oilmod.util.ExceptionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyGraph {
    private DependencyNode root;
    private Map<IDependent, DependencyNode> map = new Object2ObjectOpenHashMap<>();
    private Set<IDependency> dependencies = new ObjectOpenHashSet<>();
    private Set<IDependency> stageDeps = new ObjectOpenHashSet<>();
    List<DependencyException> exceptions = new ObjectArrayList<>();

    public void initDependent(IDependent dependent) {
        if (dependencies.size() > 0) {
            throw new IllegalStateException("Cannot add dependent at this stage");
        }
        map.put(dependent, new DependencyNode(DependencyGraph.this, dependent));
    }

    public void add(IDependency dependency) {
        DependencyNode node = map.computeIfAbsent(dependency.getDependent(), d->{throw new IllegalStateException("Added dependency of unknown dependent");});


        node.addMy(dependency);

        //this superclass of parameter
        if (IDependent.class.isAssignableFrom(dependency.getDependencyClass())) {
            dependencies.add(dependency);

            //check old dependents
            for(Map.Entry<IDependent, DependencyNode> entry:map.entrySet()) {
                IDependent dependent = entry.getKey();
                if (dependency.checkType(dependent) && dependency.checkCandidate(dependent, false)) {
                    entry.getValue().addOnMe(dependency, node);
                }
            }
        } else {
            stageDeps.add(dependency);
        }
    }

    public void consume(Object o) {
        try {
            _consume(o);
        } finally {
            if (exceptions.isEmpty())return;
            ExceptionUtils.summarizeAndClear(exceptions, String.format("Could not consume %s as exception(s) occurred", o), DependencyException::new);
        }
    }
    void _consume(Object o) {
        try {
            if (o instanceof IDependent) {
                DependencyNode node = map.get((IDependent)o);
                if (node != null) {
                    node.consumeOnMe(o);
                    return;
                }
            }
            stageDeps.stream().filter(d->d.checkType(o)&&d.checkCandidate(o, true)).forEach(dependency -> {
                DependencyNode node = map.get(dependency.getDependent());
                node.check(o, dependency);
            });
        } catch (RuntimeException e) {
            exceptions.add(new DependencyException("Could not consume " + o, e));
        }
    }


    private boolean runGraphHasRun = false;
    public void runGraph() {
        if (runGraphHasRun)throw new IllegalStateException("Cannot call runGraph more than once");
        runGraphHasRun = true;


        try {
            //we need to do this are out filter condition is affected by side effect. lol
            Set<DependencyNode> independentNotes = map.values().stream().filter(DependencyNode::areDepResolved).collect(Collectors.toSet());

            for (DependencyNode dependencyNode : independentNotes) {
                if (dependencyNode.areDepResolved()) {
                    dependencyNode.initDependant();
                }
            }
        } finally {
            if (exceptions.isEmpty())return;
            ExceptionUtils.summarizeAndClear(exceptions, "Could not finished traversing graph as exception(s) occurred", DependencyException::new);
        }

    }
}

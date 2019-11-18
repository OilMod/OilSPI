package org.oilmod.spi;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.oilmod.spi.dependencies.DependencyGraph;
import org.oilmod.spi.dependencies.DependencyPipe;
import org.oilmod.spi.mpi.IModdingPIService;
import org.oilmod.spi.provider.IMPIImplementationProvider;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MPILoader {
    private static DependencyGraph dependencyGraph;
    private static Set<Class<? extends IModdingPIService>> ignored = new ObjectOpenHashSet<>();

    public static void ignore(Class<? extends IModdingPIService>... classes) {
        ignored.addAll(Arrays.asList(classes));
    }

    @SuppressWarnings("unchecked")
    public static void init() {
        dependencyGraph = new DependencyGraph();
        ServiceLoader mpiSL = ServiceLoader.load(IModdingPIService.class);
        Map<Class<? extends IModdingPIService>, IModdingPIService> mpis = StreamSupport.stream((Spliterator<IModdingPIService<?,?>>)mpiSL.spliterator(), false)
                .collect(Collectors.toMap(IMPIClassGetter::getMPIClass, mpi->mpi));

        ServiceLoader implSL = ServiceLoader.load(IMPIImplementationProvider.class);
        Set<IMPIImplementationProvider> implSet = StreamSupport.stream((Spliterator<IMPIImplementationProvider>)implSL.spliterator(), false).collect(Collectors.toSet());

        Set<Class> implClassSet = implSet.stream().map(IMPIClassGetter::getMPIClass).collect(Collectors.toSet());

        long missing = mpis.values().stream().filter(i->!(implClassSet.contains(i.getMPIClass()) || ignored.contains(i.getMPIClass())))
                .peek(i-> System.out.printf("Missing implementation of type %s for MPI %s\n", i.getProviderClass().getSimpleName(), i.getMPIClass().getSimpleName()))
                .count();

        if (missing > 0) {
            throw new IllegalStateException("Missing " + missing + " implementations in total!");
        }

        for(IMPIImplementationProvider provider:implSet) {
            IModdingPIService mpi = mpis.get(provider.getMPIClass());
            provider.setup(mpi);
            mpi.addProvider(provider);
        }

        mpis.values().forEach(dependencyGraph::initDependent);
        implSet.forEach(dependencyGraph::initDependent);

        DependencyPipe pipe = new DependencyPipe(dependencyGraph);
        mpis.values().forEach(pipe::run);
        implSet.forEach(pipe::run);

        dependencyGraph.runGraph();
    }

    public static void commitDependency(Object o) {
        dependencyGraph.consume(o);
    }
}

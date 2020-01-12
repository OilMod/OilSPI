package org.oilmod.spi;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.Validate;
import org.oilmod.spi.dependencies.DependencyGraph;
import org.oilmod.spi.dependencies.DependencyPipe;
import org.oilmod.spi.mpi.IModdingPIService;
import org.oilmod.spi.provider.IMPIImplementationProvider;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("rawtypes")
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


        //creating default providers if needed
        mpis.values().stream().filter(i->(!implClassSet.contains(i.getMPIClass()) && i.hasDefaultProvider()))
                .peek(i-> i.addProvider(i.createDefaultProvider()))
                .forEach(i->implClassSet.add(i.getMPIClass()));


        //Making sure no actually provided MPI are ignored!
        long wronglyIgnored =  ignored.stream().filter(c -> !implClassSet.contains(c))
                .peek(i-> System.out.printf("Ignoring implementation for MPI %s even though it actually is provided!\n", i.getSimpleName()))
                .count();

        if (wronglyIgnored > 0) {
            throw new IllegalStateException(String.format("Ignoring %d  provided implementations in total!", wronglyIgnored));
        }


        //checking for missing providers (not ignored/no default)
        long missing = mpis.values().stream().filter(i->!(implClassSet.contains(i.getMPIClass()) || ignored.contains(i.getMPIClass())))
                .peek(i-> System.out.printf("Missing implementation of type %s for MPI %s\n", i.getProviderClass().getSimpleName(), i.getMPIClass().getSimpleName()))
                .count();

        if (missing > 0) {
            throw new IllegalStateException(String.format("Missing %d implementations in total! For %d of these this is ignored", missing +  ignored.size(), ignored.size()));
        }

        for(IMPIImplementationProvider provider:implSet) {
            IModdingPIService mpi = mpis.get(provider.getMPIClass());
            Validate.notNull(mpi, "Could not find mpi with class %s",  provider.getMPIClass());
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


    @SuppressWarnings("unchecked")
    public static void validateGenerics(Class[] mpi, Class[] impl, Class implGetClass, boolean implBase) {
        Validate.isTrue(mpi[0] == impl[0], "Malformed generics, mpi does not match: %s vs %s", mpi[0], impl[0]);
        Validate.isTrue(mpi[1].isAssignableFrom(impl[1]), "Malformed generics, implementation base does not match: %s is not assignable from %s", mpi[1], impl[1]);
        if (implBase) {
            Validate.isTrue(impl[2]==null, "Malformed generics, implementation base has implementation assigned! implementation base needs to be generic!");
        } else {
            Validate.notNull(impl[2], "Could not resolve generics for impl: %1$s needs to extend %2$s generically! wrong: 'extends %2$s' correct: 'extends %2$s<%1$s>'", implGetClass.getSimpleName(), impl[1].getSimpleName());
            Validate.isTrue(impl[1].isAssignableFrom(impl[2]), "Malformed generics, implementation does not match: %s is not assignable from %s", impl[1], impl[2]);
        }


    }
}

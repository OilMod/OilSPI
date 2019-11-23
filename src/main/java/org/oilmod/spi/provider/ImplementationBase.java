package org.oilmod.spi.provider;

import org.oilmod.spi.MPILoader;
import org.oilmod.spi.mpi.IModdingPIService;
import org.oilmod.util.Strings;

import static org.oilmod.util.ReflectionUtils.resolveGenericSuperInterface;

public abstract class ImplementationBase<MPI extends IModdingPIService<MPI, IB>, IB extends ImplementationBase<MPI, IB, ? extends IB>, Impl extends IB>  implements IMPIImplementationProvider<MPI, IB, Impl> {
    private MPI mpi;
    private final Class<MPI> mpiClass;
    private final Class<IB> ibClass;

    public ImplementationBase(Class<MPI> mpiClass, Class<IB> ibClass) {
        this.mpiClass = mpiClass;
        this.ibClass = ibClass;
    }


    @SuppressWarnings("unchecked")
    public ImplementationBase() {
        Class<?>[] generics = resolveGenericSuperInterface(getClass(), IMPIImplementationProvider.class);
        this.mpiClass = (Class<MPI>) generics[0];
        this.ibClass = (Class<IB>) generics[1];

        Class<?>[] mpi = resolveGenericSuperInterface(mpiClass, IModdingPIService.class);
        MPILoader.validateGenerics(mpi, generics, getClass(), false);
    }

    @Override
    public Class<MPI> getMPIClass() {
        return mpiClass;
    }

    @Override
    public Class<IB> getImplementationBaseClass() {
        return ibClass;
    }

    @Override
    public void setup(MPI mpi) {
        if (this.mpi != null) throw new IllegalStateException("Cannot change MBI, this is set by the serviceloader!");
        this.mpi = mpi;
    }

    @Override
    public MPI getMpi() {
        return mpi;
    }
}

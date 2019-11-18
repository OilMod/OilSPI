package org.oilmod.spi.provider;

import org.oilmod.spi.mpi.IModdingPIService;

public abstract class ImplementationBase<MPI extends IModdingPIService<MPI, IB>, IB extends ImplementationBase<MPI, IB, ? extends IB>, Impl extends IB>  implements IMPIImplementationProvider<MPI, IB, Impl> {
    private MPI mpi;
    private final Class<MPI> mpiClass;
    private final Class<IB> ibClass;

    public ImplementationBase(Class<MPI> mpiClass, Class<IB> ibClass) {
        this.mpiClass = mpiClass;
        this.ibClass = ibClass;
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

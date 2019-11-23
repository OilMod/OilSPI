package org.oilmod.spi.provider;

import org.oilmod.spi.IMPIClassGetter;
import org.oilmod.spi.dependencies.IDependent;
import org.oilmod.spi.mpi.IModdingPIService;

import static org.oilmod.util.LamdbaCastUtils.cast;

public interface IMPIImplementationProvider<MPI extends IModdingPIService<MPI, IB>, IB extends IMPIImplementationProvider<MPI, IB, ? extends IB>, Impl extends IB>  extends IMPIClassGetter<MPI, IB>, IDependent {

    Class<MPI> getMPIClass();
    Class<IB> getImplementationBaseClass();
    default Class<? extends Impl> getImplementationClass() {
        return cast(getClass());
    }

    /**
     * Should only be allowed once
     * @param mpi
     */
    void setup(MPI mpi);
    MPI getMpi();

    @Override
    default Class[] getDependentIdentifierClasses() {
        return new Class[]{getImplementationClass(), getImplementationBaseClass()}; //two identifiers Base and MBI have 1to1 mapping
    }

    @Override
    default Class<IB> getProviderClass() {
        return getImplementationBaseClass();
    }

    default void onReady() {

    }

    @Override
    default void allDepResolved() {
        IDependent.super.allDepResolved();
        onReady();
    }
}

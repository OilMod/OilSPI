package org.oilmod.spi.test;

import org.oilmod.spi.mpi.IModdingPIService;
import org.oilmod.spi.provider.ImplementationBase;

//used to check if the algorithm to resolve the generics is solid within its specifications at least (year i know 1 example is not enough fussing
public class SwapGenerics<IB extends ImplementationBase<MPI, IB, ? extends IB>, Impl extends IB, MPI extends IModdingPIService<MPI, IB>> extends ImplementationBase<MPI, IB, Impl> {
}

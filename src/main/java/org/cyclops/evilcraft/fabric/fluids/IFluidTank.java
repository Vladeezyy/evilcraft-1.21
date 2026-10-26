package org.cyclops.evilcraft.fabric.fluids;

import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;

/**
 * A single fluid tank.
 */
public interface IFluidTank {

    FluidStack getFluid();

    int getFluidAmount();

    int getCapacity();

    boolean isFluidValid(FluidStack stack);

    int fill(FluidStack resource, IFluidHandler.FluidAction action);

    FluidStack drain(int maxDrain, IFluidHandler.FluidAction action);

    FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action);
}

package org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid;



import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;

/**
 * A fluid handler that is mutable.
 * @author rubensworks
 */
public interface IFluidHandlerMutable extends IFluidHandler {

    public void setFluidInTank(int tank, FluidStack fluidStack);

}

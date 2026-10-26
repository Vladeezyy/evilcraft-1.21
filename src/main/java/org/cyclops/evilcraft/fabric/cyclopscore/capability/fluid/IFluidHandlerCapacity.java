package org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid;



import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;

/**
 * A fluid handler with a mutable capacity.
 * @author rubensworks
 */
public interface IFluidHandlerCapacity extends IFluidHandler {

    public void setTankCapacity(int tank, int capacity);
    public int getTankCapacity(int tank);

}

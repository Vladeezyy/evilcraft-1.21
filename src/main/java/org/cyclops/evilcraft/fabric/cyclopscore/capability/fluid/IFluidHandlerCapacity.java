package org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid;



import org.cyclops.evilcraft.fabric.transfer.ResourceHandler;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;
import org.cyclops.evilcraft.fabric.transfer.TransactionContext;

/**
 * A fluid handler with a mutable capacity.
 * @author rubensworks
 */
public interface IFluidHandlerCapacity extends ResourceHandler<FluidResource> {

    public void setTankCapacity(int tank, int capacity, TransactionContext transaction);

    public int getTankCapacity(int tank);

}

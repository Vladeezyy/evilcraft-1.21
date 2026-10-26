package org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid;



import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandlerItem;

/**
 * An item fluid handler with a mutable capacity.
 * @author rubensworks
 */
public interface IFluidHandlerItemCapacity extends IFluidHandlerItem {

    public void setCapacity(int capacity);
    public int getCapacity();

}

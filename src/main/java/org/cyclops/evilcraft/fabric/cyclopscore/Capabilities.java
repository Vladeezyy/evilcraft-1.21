package org.cyclops.evilcraft.fabric.cyclopscore;

import org.cyclops.evilcraft.Reference;


import net.minecraft.resources.ResourceLocation;
import org.cyclops.evilcraft.fabric.capabilities.ItemCapability;
import org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid.IFluidHandlerItemCapacity;

/**
 * Used capabilities for this mod.
 * @author rubensworks
 */
public class Capabilities {
    public static class Item {
        public static final ItemCapability<IFluidHandlerItemCapacity, Void> FLUID_HANDLER_CAPACITY = ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fluid_handler_item_capacity"), IFluidHandlerItemCapacity.class);
    }
}

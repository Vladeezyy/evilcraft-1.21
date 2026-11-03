package org.cyclops.evilcraft.fabric.cyclopscore.inventory.slot;



import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import javax.annotation.Nullable;

/**
 * Slots that will accept buckets and fluid containers.
 * @author rubensworks
 *
 */
public class SlotFluidContainer extends Slot {

    @Nullable
    private final Fluid fluid;

    public SlotFluidContainer(Container inventory, int index, int x, int y, @Nullable Fluid fluid) {
        super(inventory, index, x, y);
        this.fluid = fluid;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return checkIsItemValid(itemStack, fluid);
    }

    public static boolean checkIsItemValid(ItemStack itemStack, @Nullable Fluid fluid) {
        if (!itemStack.isEmpty()) {
            ItemAccess itemAccess = ItemAccess.forStack(itemStack);
            ResourceHandler<FluidResource> fluidHandler = Capabilities.Fluid.ITEM.getCapability(itemAccess, null);
            if (fluidHandler == null) {
                return false;
            }
            if (fluid == null) {
                return true;
            }
            for (int i = 0; i < fluidHandler.size(); i++) {
                if (fluidHandler.isValid(0, FluidResource.of(fluid))) {
                    return true;
                }
            }
        }

        return false;
    }

}

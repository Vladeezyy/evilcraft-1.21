package org.cyclops.evilcraft.fabric.cyclopscore.inventory.slot;



import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.transfer.ResourceHandler;
import org.cyclops.evilcraft.fabric.transfer.ItemAccess;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;

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
            ResourceHandler<FluidResource> fluidHandler = itemAccess.getCapability(Capabilities.Fluid.ITEM);
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

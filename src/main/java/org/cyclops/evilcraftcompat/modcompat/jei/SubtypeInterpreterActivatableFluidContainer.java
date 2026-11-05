package org.cyclops.evilcraftcompat.modcompat.jei;

import org.cyclops.evilcraft.fabric.transfer.FluidUtil;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author rubensworks
 */
public class SubtypeInterpreterActivatableFluidContainer implements ISubtypeInterpreter<ItemStack> {

    @Override
    public @Nullable Object getSubtypeData(ItemStack itemStack, UidContext context) {
        FluidStack fluidStack = FluidUtil.getFirstStackContained(itemStack);
        return fluidStack.getFluid();
    }
}

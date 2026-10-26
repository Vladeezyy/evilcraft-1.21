package org.cyclops.evilcraft.fabric.cyclopscore.recipe.type;



import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;
import org.cyclops.evilcraft.fabric.fluids.capability.templates.FluidTank;

/**
 * Default implementation of {@link IInventoryFluid}.
 * @author rubensworks
 */
public class InventoryFluid implements IInventoryFluid {

    // Not a Container (as in Cyclops Core on NeoForge): a class that is both a Container and a RecipeInput cannot be
    // remapped to intermediary names (isEmpty, getItem have different names in the two interfaces)
    private final NonNullList<ItemStack> itemStacks;
    private final IFluidHandler fluidHandler;

    public InventoryFluid(NonNullList<ItemStack> itemStacks, NonNullList<FluidStack> fluidStacks) {
        this.itemStacks = NonNullList.withSize(itemStacks.size(), ItemStack.EMPTY);
        for (int slot = 0; slot < itemStacks.size(); slot++) {
            this.itemStacks.set(slot, itemStacks.get(slot));
        }
        if (fluidStacks.size() == 1) {
            this.fluidHandler = new FluidTank(Integer.MAX_VALUE);
            this.fluidHandler.fill(fluidStacks.get(0), IFluidHandler.FluidAction.EXECUTE);
        } else {
            this.fluidHandler = new FluidHandlerListReadOnly(fluidStacks);
        }
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getFluidHandler().getTanks(); i++) {
            if (!getFluidHandler().getFluidInTank(i).isEmpty()) {
                return false;
            }
        }
        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }

    @Override
    public ItemStack getItem(int index) {
        return itemStacks.get(index);
    }

    @Override
    public int size() {
        return itemStacks.size();
    }
}

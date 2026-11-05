package org.cyclops.evilcraft.fabric.cyclopscore.recipe.type;



import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.transfer.ResourceHandler;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;
import org.cyclops.evilcraft.fabric.transfer.FluidStacksResourceHandler;

/**
 * Default implementation of {@link IInventoryFluid}.
 * @author rubensworks
 */
public class InventoryFluid implements IInventoryFluid {

    private final NonNullList<ItemStack> itemStacks;
    private final ResourceHandler<FluidResource> fluidHandler;

    public InventoryFluid(NonNullList<ItemStack> itemStacks, NonNullList<FluidStack> fluidStacks) {
        this.itemStacks = NonNullList.withSize(itemStacks.size(), ItemStack.EMPTY);
        for (int slot = 0; slot < itemStacks.size(); slot++) {
            this.itemStacks.set(slot, itemStacks.get(slot));
        }
        this.fluidHandler = new FluidStacksResourceHandler(fluidStacks, Integer.MAX_VALUE);
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getFluidHandler().size(); i++) {
            if (!getFluidHandler().getResource(i).isEmpty()) {
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
    public ResourceHandler<FluidResource> getFluidHandler() {
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

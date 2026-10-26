package org.cyclops.evilcraft.fabric.cyclopscore.recipe.type;



import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;
import org.cyclops.evilcraft.fabric.fluids.capability.templates.FluidTank;

/**
 * Default implementation of {@link IInventoryFluid}.
 * @author rubensworks
 */
public class InventoryFluid extends TransientCraftingContainer implements IInventoryFluid {

    private final IFluidHandler fluidHandler;

    public InventoryFluid(NonNullList<ItemStack> itemStacks, NonNullList<FluidStack> fluidStacks) {
        super(new AbstractContainerMenu(null, 0) {
            @Override
            public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player playerIn) {
                return false;
            }
        }, itemStacks.size(), 1);
        int slot = 0;
        for (ItemStack itemStack : itemStacks) {
            setItem(slot++, itemStack);
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
        return super.isEmpty();
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }

    @Override
    public int size() {
        return getContainerSize();
    }
}

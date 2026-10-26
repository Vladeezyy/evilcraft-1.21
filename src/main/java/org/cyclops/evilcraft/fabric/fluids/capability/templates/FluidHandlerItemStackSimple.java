package org.cyclops.evilcraft.fabric.fluids.capability.templates;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.SimpleFluidContent;

import java.util.function.Supplier;

/**
 * An item fluid handler that is either empty or completely full.
 */
public class FluidHandlerItemStackSimple extends FluidHandlerItemStack {

    public FluidHandlerItemStackSimple(Supplier<DataComponentType<SimpleFluidContent>> componentType, ItemStack container, int capacity) {
        super(componentType, container, capacity);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !canFillFluidType(resource) || !getFluid().isEmpty()
                || resource.getAmount() < capacity) {
            return 0;
        }
        if (action.execute()) {
            setFluid(resource.copyWithAmount(capacity));
        }
        return capacity;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        FluidStack contained = getFluid();
        if (contained.isEmpty() || !canDrainFluidType(contained) || contained.getAmount() > maxDrain) {
            return FluidStack.EMPTY;
        }
        if (action.execute()) {
            setContainerToEmpty();
        }
        return contained;
    }

    /**
     * Replaces the container with another item stack once it has been emptied.
     */
    public static class SwapEmpty extends FluidHandlerItemStackSimple {
        protected final ItemStack emptyContainer;

        public SwapEmpty(Supplier<DataComponentType<SimpleFluidContent>> componentType, ItemStack container, ItemStack emptyContainer, int capacity) {
            super(componentType, container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            container = emptyContainer.copy();
        }
    }
}

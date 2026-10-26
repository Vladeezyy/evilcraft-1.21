package org.cyclops.evilcraft.fabric.fluids.capability.templates;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.SimpleFluidContent;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandlerItem;

import java.util.function.Supplier;

/**
 * An item fluid handler that keeps its fluid in a data component and accepts partial fills and drains.
 */
public class FluidHandlerItemStack implements IFluidHandlerItem {

    protected final Supplier<DataComponentType<SimpleFluidContent>> componentType;
    protected ItemStack container;
    protected int capacity;

    public FluidHandlerItemStack(Supplier<DataComponentType<SimpleFluidContent>> componentType, ItemStack container, int capacity) {
        this.componentType = componentType;
        this.container = container;
        this.capacity = capacity;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    public FluidStack getFluid() {
        return container.getOrDefault(componentType.get(), SimpleFluidContent.EMPTY).copy();
    }

    protected void setFluid(FluidStack fluid) {
        container.set(componentType.get(), SimpleFluidContent.copyOf(fluid));
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !canFillFluidType(resource)) {
            return 0;
        }
        FluidStack contained = getFluid();
        if (contained.isEmpty()) {
            int filled = Math.min(capacity, resource.getAmount());
            if (action.execute() && filled > 0) {
                setFluid(resource.copyWithAmount(filled));
            }
            return filled;
        }
        if (!FluidStack.isSameFluidSameComponents(contained, resource)) {
            return 0;
        }
        int filled = Math.max(0, Math.min(capacity - contained.getAmount(), resource.getAmount()));
        if (action.execute() && filled > 0) {
            contained.grow(filled);
            setFluid(contained);
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, getFluid())) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        FluidStack contained = getFluid();
        if (contained.isEmpty() || !canDrainFluidType(contained)) {
            return FluidStack.EMPTY;
        }
        int drained = Math.min(contained.getAmount(), maxDrain);
        FluidStack result = contained.copyWithAmount(drained);
        if (action.execute()) {
            contained.shrink(drained);
            if (contained.isEmpty()) {
                setContainerToEmpty();
            } else {
                setFluid(contained);
            }
        }
        return result;
    }

    public boolean canFillFluidType(FluidStack fluid) {
        return true;
    }

    public boolean canDrainFluidType(FluidStack fluid) {
        return true;
    }

    protected void setContainerToEmpty() {
        container.remove(componentType.get());
    }
}

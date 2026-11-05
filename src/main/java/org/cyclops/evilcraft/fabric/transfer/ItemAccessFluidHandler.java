package org.cyclops.evilcraft.fabric.transfer;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import org.cyclops.evilcraft.fabric.fluids.SimpleFluidContent;

/**
 * One tank stored in a data component of the item.
 */
public class ItemAccessFluidHandler extends ItemAccessResourceHandler<FluidResource> {

    protected final Item validItem;
    protected final DataComponentType<SimpleFluidContent> component;
    protected int capacity;

    public ItemAccessFluidHandler(ItemAccess itemAccess, DataComponentType<SimpleFluidContent> component, int capacity) {
        super(itemAccess, 1);
        this.validItem = itemAccess.getResource().getItem();
        this.component = component;
        this.capacity = capacity;
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource accessResource, int index) {
        return accessResource.is(validItem) ? FluidResource.of(accessResource.getOrDefault(component, SimpleFluidContent.EMPTY).copy())
                : FluidResource.EMPTY;
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        return accessResource.is(validItem) ? accessResource.getOrDefault(component, SimpleFluidContent.EMPTY).getAmount() : 0;
    }

    @Override
    protected ItemResource update(ItemResource accessResource, int index, FluidResource newResource, int newAmount) {
        return accessResource.with(component, SimpleFluidContent.copyOf(newResource.toStack(newAmount)));
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return itemAccess.getResource().is(validItem);
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) {
        return capacity;
    }
}

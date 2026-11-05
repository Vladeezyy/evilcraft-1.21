package org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid;



import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.transfer.ItemAccessResourceHandler;
import org.cyclops.evilcraft.fabric.transfer.TransferPreconditions;
import org.cyclops.evilcraft.fabric.transfer.ItemAccess;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;
import org.cyclops.evilcraft.fabric.transfer.ItemResource;
import org.cyclops.evilcraft.fabric.transfer.TransactionContext;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A fluid resource handler that swaps an item to empty when drained.
 * Inspired by NeoForge's BucketResourceHandler.
 * @author rubensworks
 */
public class ResourceHandlerFluidSwapEmpty extends ItemAccessResourceHandler<FluidResource> {

    private final FluidStack fluid;
    private final ItemResource itemResourceFull;
    private final ItemResource itemResourceEmpty;

    public ResourceHandlerFluidSwapEmpty(ItemAccess itemAccess, FluidStack fluid, ItemResource itemResourceFull, ItemResource itemResourceEmpty) {
        super(itemAccess, 1);
        this.fluid = fluid;
        this.itemResourceFull = itemResourceFull;
        this.itemResourceEmpty = itemResourceEmpty;
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource accessResource, int index) {
        if (itemResourceEmpty.is(accessResource.getItem())) {
            return FluidResource.EMPTY;
        }
        return FluidResource.of(fluid);
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        var resource = getResourceFrom(accessResource, index);
        return resource.isEmpty() ? 0 : fluid.getAmount();
    }

    @Nullable
    @Override
    protected ItemResource update(ItemResource accessResource, int index, FluidResource newResource, int newAmount) {
        if (newAmount == 0) {
            return itemResourceEmpty;
        } else if (newAmount != fluid.getAmount()) {
            return null;
        } else {
            return this.itemResourceFull;
        }
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) {
        Objects.checkIndex(index, size());
        return fluid.getAmount();
    }

    // A copy of ItemAccessResourceHandler#extract modified to support empty result stacks.
    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, this.size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int accessAmount = this.itemAccess.getAmount();
        if (accessAmount == 0) {
            return 0;
        } else {
            ItemResource accessResource = this.itemAccess.getResource();
            FluidResource currentResource = this.getResourceFrom(accessResource, index);
            if (resource.equals(currentResource)) {
                int currentAmountPerItem = this.getAmountFrom(accessResource, index);
                int extractedPerItem = Math.min(amount / accessAmount, currentAmountPerItem);
                if (extractedPerItem > 0) {
                    ItemResource emptiedResource = this.update(accessResource, index, resource, currentAmountPerItem - extractedPerItem);
                    if (emptiedResource != null) { // This is added!
                        if (!emptiedResource.isEmpty()) {
                            return extractedPerItem * this.itemAccess.exchange(emptiedResource, accessAmount, transaction);
                        } else {
                            // This is added!
                            return extractedPerItem * this.itemAccess.extract(accessResource, accessAmount, transaction);
                        }
                    }
                }
            }

            return 0;
        }
    }

}

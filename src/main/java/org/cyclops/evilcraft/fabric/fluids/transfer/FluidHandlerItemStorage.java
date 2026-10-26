package org.cyclops.evilcraft.fabric.fluids.transfer;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.capabilities.ICapabilityProvider;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Exposes the fluid handler of an EvilCraft item as a Fabric fluid storage of a container item context:
 * changes are made on a copy of the stack, which is then exchanged in the context.
 */
public class FluidHandlerItemStorage implements Storage<FluidVariant> {

    private final ContainerItemContext context;
    private final ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> provider;

    public FluidHandlerItemStorage(ContainerItemContext context, ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> provider) {
        this.context = context;
        this.provider = provider;
    }

    @Nullable
    private IFluidHandlerItem handler() {
        ItemVariant variant = context.getItemVariant();
        if (variant.isBlank() || context.getAmount() <= 0) {
            return null;
        }
        return provider.getCapability(variant.toStack(1), null);
    }

    private long exchange(IFluidHandlerItem handler, TransactionContext transaction) {
        return context.exchange(ItemVariant.of(handler.getContainer()), 1, transaction);
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        int mb = FluidAmounts.toMb(maxAmount);
        IFluidHandlerItem handler = handler();
        if (handler == null || resource.isBlank() || mb <= 0) {
            return 0;
        }
        int filled = handler.fill(FluidStack.fromVariant(resource, mb), IFluidHandler.FluidAction.EXECUTE);
        if (filled > 0 && exchange(handler, transaction) == 1) {
            return FluidAmounts.toDroplets(filled);
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        int mb = FluidAmounts.toMb(maxAmount);
        IFluidHandlerItem handler = handler();
        if (handler == null || resource.isBlank() || mb <= 0) {
            return 0;
        }
        FluidStack drained = handler.drain(FluidStack.fromVariant(resource, mb), IFluidHandler.FluidAction.EXECUTE);
        if (!drained.isEmpty() && exchange(handler, transaction) == 1) {
            return FluidAmounts.toDroplets(drained.getAmount());
        }
        return 0;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        IFluidHandlerItem handler = handler();
        if (handler == null) {
            return java.util.Collections.emptyIterator();
        }
        List<StorageView<FluidVariant>> views = Lists.newArrayListWithCapacity(handler.getTanks());
        for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack fluid = handler.getFluidInTank(i);
            long capacity = FluidAmounts.toDroplets(handler.getTankCapacity(i));
            views.add(new StorageView<>() {
                @Override
                public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
                    return fluid.isEmpty() || !resource.equals(fluid.toVariant()) ? 0
                            : FluidHandlerItemStorage.this.extract(resource, maxAmount, transaction);
                }

                @Override
                public boolean isResourceBlank() {
                    return fluid.isEmpty();
                }

                @Override
                public FluidVariant getResource() {
                    return fluid.toVariant();
                }

                @Override
                public long getAmount() {
                    return FluidAmounts.toDroplets(fluid.getAmount());
                }

                @Override
                public long getCapacity() {
                    return capacity;
                }
            });
        }
        return views.iterator();
    }
}

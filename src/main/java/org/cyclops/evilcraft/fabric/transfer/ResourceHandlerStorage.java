package org.cyclops.evilcraft.fabric.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Iterator;
import java.util.function.Function;

/**
 * A handler of this mod as a Fabric storage, for hoppers and pipes. A fluid moves in whole millibuckets.
 */
public final class ResourceHandlerStorage<T extends Resource, V> implements SlottedStorage<V> {

    public static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000;

    private final ResourceHandler<T> handler;
    private final Function<V, T> toResource;
    private final Function<T, V> toVariant;
    private final long unit;

    private ResourceHandlerStorage(ResourceHandler<T> handler, Function<V, T> toResource, Function<T, V> toVariant, long unit) {
        this.handler = handler;
        this.toResource = toResource;
        this.toVariant = toVariant;
        this.unit = unit;
    }

    public static ResourceHandlerStorage<FluidResource, FluidVariant> ofFluid(ResourceHandler<FluidResource> handler) {
        return new ResourceHandlerStorage<>(handler, FluidResource::of, FluidResource::variant, DROPLETS_PER_MB);
    }

    public static ResourceHandlerStorage<ItemResource, ItemVariant> ofItem(ResourceHandler<ItemResource> handler) {
        return new ResourceHandlerStorage<>(handler, ItemResource::of, ItemResource::variant, 1);
    }

    public ResourceHandler<T> handler() {
        return handler;
    }

    private int units(long amount) {
        return (int) Math.min(Integer.MAX_VALUE, amount / unit);
    }

    @Override
    public long insert(V resource, long maxAmount, TransactionContext transaction) {
        T converted = toResource.apply(resource);
        int amount = units(maxAmount);
        return converted.isEmpty() || amount <= 0 ? 0
                : handler.insert(converted, amount, org.cyclops.evilcraft.fabric.transfer.TransactionContext.wrap(transaction)) * unit;
    }

    @Override
    public long extract(V resource, long maxAmount, TransactionContext transaction) {
        T converted = toResource.apply(resource);
        int amount = units(maxAmount);
        return converted.isEmpty() || amount <= 0 ? 0
                : handler.extract(converted, amount, org.cyclops.evilcraft.fabric.transfer.TransactionContext.wrap(transaction)) * unit;
    }

    @Override
    public int getSlotCount() {
        return handler.size();
    }

    @Override
    public SingleSlotStorage<V> getSlot(int index) {
        return new Slot(index);
    }

    @Override
    public Iterator<StorageView<V>> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < handler.size();
            }

            @Override
            public StorageView<V> next() {
                return new Slot(index++);
            }
        };
    }

    private final class Slot implements SingleSlotStorage<V> {
        private final int index;

        private Slot(int index) {
            this.index = index;
        }

        @Override
        public long insert(V resource, long maxAmount, TransactionContext transaction) {
            T converted = toResource.apply(resource);
            int amount = units(maxAmount);
            return converted.isEmpty() || amount <= 0 ? 0 : handler.insert(index, converted, amount,
                    org.cyclops.evilcraft.fabric.transfer.TransactionContext.wrap(transaction)) * unit;
        }

        @Override
        public long extract(V resource, long maxAmount, TransactionContext transaction) {
            T converted = toResource.apply(resource);
            int amount = units(maxAmount);
            return converted.isEmpty() || amount <= 0 ? 0 : handler.extract(index, converted, amount,
                    org.cyclops.evilcraft.fabric.transfer.TransactionContext.wrap(transaction)) * unit;
        }

        @Override
        public boolean isResourceBlank() {
            return handler.getResource(index).isEmpty();
        }

        @Override
        public V getResource() {
            return toVariant.apply(handler.getResource(index));
        }

        @Override
        public long getAmount() {
            return handler.getAmountAsLong(index) * unit;
        }

        @Override
        public long getCapacity() {
            return handler.getCapacityAsLong(index, handler.getResource(index)) * unit;
        }
    }
}

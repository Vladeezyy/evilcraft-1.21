package org.cyclops.evilcraft.fabric.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;

import java.util.Iterator;
import java.util.function.Function;

/**
 * A Fabric storage (of vanilla or another mod) as a handler. A fluid moves in whole millibuckets, what is left over
 * in droplets stays where it was.
 */
public final class StorageResourceHandler<T extends Resource, V> implements ResourceHandler<T> {

    private final Storage<V> storage;
    private final Function<V, T> toResource;
    private final Function<T, V> toVariant;
    private final T empty;
    private final long unit;

    private StorageResourceHandler(Storage<V> storage, Function<V, T> toResource, Function<T, V> toVariant, T empty, long unit) {
        this.storage = storage;
        this.toResource = toResource;
        this.toVariant = toVariant;
        this.empty = empty;
        this.unit = unit;
    }

    public static ResourceHandler<FluidResource> ofFluid(Storage<FluidVariant> storage) {
        if (storage instanceof ResourceHandlerStorage<?, ?> wrapper) {
            return unwrap(wrapper);
        }
        return new StorageResourceHandler<>(storage, FluidResource::of, FluidResource::variant, FluidResource.EMPTY,
                ResourceHandlerStorage.DROPLETS_PER_MB);
    }

    public static ResourceHandler<ItemResource> ofItem(Storage<ItemVariant> storage) {
        if (storage instanceof ResourceHandlerStorage<?, ?> wrapper) {
            return unwrap(wrapper);
        }
        return new StorageResourceHandler<>(storage, ItemResource::of, ItemResource::variant, ItemResource.EMPTY, 1);
    }

    @SuppressWarnings("unchecked")
    private static <R extends Resource> ResourceHandler<R> unwrap(ResourceHandlerStorage<?, ?> wrapper) {
        return (ResourceHandler<R>) wrapper.handler();
    }

    private StorageView<V> view(int index) {
        if (storage instanceof SlottedStorage<V> slotted) {
            return index < slotted.getSlotCount() ? slotted.getSlot(index) : null;
        }
        Iterator<StorageView<V>> iterator = storage.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            StorageView<V> view = iterator.next();
            if (i == index) {
                return view;
            }
        }
        return null;
    }

    @Override
    public int size() {
        if (storage instanceof SlottedStorage<V> slotted) {
            return slotted.getSlotCount();
        }
        int size = 0;
        for (Iterator<StorageView<V>> iterator = storage.iterator(); iterator.hasNext(); iterator.next()) {
            size++;
        }
        return size;
    }

    @Override
    public T getResource(int index) {
        StorageView<V> view = view(index);
        return view == null || view.isResourceBlank() ? empty : toResource.apply(view.getResource());
    }

    @Override
    public long getAmountAsLong(int index) {
        StorageView<V> view = view(index);
        return view == null || view.isResourceBlank() ? 0 : view.getAmount() / unit;
    }

    @Override
    public long getCapacityAsLong(int index, T resource) {
        StorageView<V> view = view(index);
        return view == null ? 0 : view.getCapacity() / unit;
    }

    @Override
    public boolean isValid(int index, T resource) {
        return storage.supportsInsertion();
    }

    /**
     * Run a move, again with the amount rounded down if the storage moved a part of a unit.
     */
    private int whole(TransactionContext transaction, int amount, Mover mover) {
        try (Transaction nested = Transaction.open(transaction)) {
            long moved = mover.move(amount * unit, nested);
            if (moved % unit == 0) {
                nested.commit();
                return (int) (moved / unit);
            }
            amount = (int) (moved / unit);
        }
        if (amount == 0) {
            return 0;
        }
        try (Transaction nested = Transaction.open(transaction)) {
            long moved = mover.move(amount * unit, nested);
            if (moved == amount * unit) {
                nested.commit();
                return amount;
            }
        }
        return 0;
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        V variant = toVariant.apply(resource);
        if (storage instanceof SlottedStorage<V> slotted) {
            if (index >= slotted.getSlotCount()) {
                return 0;
            }
            return whole(transaction, amount, (max, tx) -> slotted.getSlot(index).insert(variant, max, tx.fabric()));
        }
        // Other storages insert as a whole, not per view
        return index == 0 ? insert(resource, amount, transaction) : 0;
    }

    @Override
    public int insert(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        V variant = toVariant.apply(resource);
        return whole(transaction, amount, (max, tx) -> storage.insert(variant, max, tx.fabric()));
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        StorageView<V> view = view(index);
        if (view == null) {
            return 0;
        }
        V variant = toVariant.apply(resource);
        return whole(transaction, amount, (max, tx) -> view.extract(variant, max, tx.fabric()));
    }

    @Override
    public int extract(T resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        V variant = toVariant.apply(resource);
        return whole(transaction, amount, (max, tx) -> storage.extract(variant, max, tx.fabric()));
    }

    @FunctionalInterface
    private interface Mover {
        long move(long max, Transaction transaction);
    }
}

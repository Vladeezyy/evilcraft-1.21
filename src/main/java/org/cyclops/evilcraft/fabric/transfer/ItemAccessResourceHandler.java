package org.cyclops.evilcraft.fabric.transfer;

import java.util.Objects;

/**
 * A handler whose contents are stored on the item of an {@link ItemAccess}: amounts are per item, times the stack size.
 */
public abstract class ItemAccessResourceHandler<T extends Resource> implements ResourceHandler<T> {

    protected final ItemAccess itemAccess;
    protected final int size;

    protected ItemAccessResourceHandler(ItemAccess itemAccess, int size) {
        this.itemAccess = itemAccess;
        this.size = size;
    }

    protected abstract T getResourceFrom(ItemResource accessResource, int index);

    protected abstract int getAmountFrom(ItemResource accessResource, int index);

    /**
     * @return The item with these contents, null if the contents are not possible.
     */
    protected abstract ItemResource update(ItemResource accessResource, int index, T newResource, int newAmount);

    @Override
    public boolean isValid(int index, T resource) {
        return true;
    }

    protected abstract int getCapacity(int index, T resource);

    @Override
    public int size() {
        return size;
    }

    @Override
    public T getResource(int index) {
        Objects.checkIndex(index, size);
        return getResourceFrom(itemAccess.getResource(), index);
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size);
        return (long) itemAccess.getAmount() * getAmountFrom(itemAccess.getResource(), index);
    }

    @Override
    public long getCapacityAsLong(int index, T resource) {
        Objects.checkIndex(index, size);
        return resource.isEmpty() || isValid(index, resource) ? (long) itemAccess.getAmount() * getCapacity(index, resource) : 0;
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size);
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int items = itemAccess.getAmount();
        if (items == 0) {
            return 0;
        }
        ItemResource item = itemAccess.getResource();
        int current = getAmountFrom(item, index);
        if ((current == 0 || resource.equals(getResourceFrom(item, index))) && isValid(index, resource)) {
            int insertedPerItem = Math.min(amount / items, getCapacity(index, resource) - current);
            if (insertedPerItem > 0) {
                ItemResource filled = update(item, index, resource, current + insertedPerItem);
                if (filled != null && !filled.isEmpty()) {
                    return insertedPerItem * itemAccess.exchange(filled, items, transaction);
                }
            }
        }
        return 0;
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size);
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int items = itemAccess.getAmount();
        if (items == 0) {
            return 0;
        }
        ItemResource item = itemAccess.getResource();
        if (resource.equals(getResourceFrom(item, index))) {
            int current = getAmountFrom(item, index);
            int extractedPerItem = Math.min(amount / items, current);
            if (extractedPerItem > 0) {
                ItemResource emptied = update(item, index, resource, current - extractedPerItem);
                if (emptied != null && !emptied.isEmpty()) {
                    return extractedPerItem * itemAccess.exchange(emptied, items, transaction);
                }
            }
        }
        return 0;
    }
}

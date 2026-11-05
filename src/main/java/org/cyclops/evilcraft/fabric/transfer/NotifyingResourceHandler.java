package org.cyclops.evilcraft.fabric.transfer;

/**
 * A handler that runs a callback after the outer transaction that moved something committed. Goes through the indexed
 * methods too: hoppers extract per slot, not from the storage as a whole.
 */
public final class NotifyingResourceHandler<T extends Resource> implements ResourceHandler<T> {

    private final ResourceHandler<T> delegate;
    private final Runnable onCommit;

    public NotifyingResourceHandler(ResourceHandler<T> delegate, Runnable onCommit) {
        this.delegate = delegate;
        this.onCommit = onCommit;
    }

    private int moved(int amount, TransactionContext transaction) {
        if (amount != 0) {
            new RootCommitJournal(onCommit).updateSnapshots(transaction);
        }
        return amount;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public T getResource(int index) {
        return delegate.getResource(index);
    }

    @Override
    public long getAmountAsLong(int index) {
        return delegate.getAmountAsLong(index);
    }

    @Override
    public long getCapacityAsLong(int index, T resource) {
        return delegate.getCapacityAsLong(index, resource);
    }

    @Override
    public boolean isValid(int index, T resource) {
        return delegate.isValid(index, resource);
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        return moved(delegate.insert(index, resource, amount, transaction), transaction);
    }

    @Override
    public int insert(T resource, int amount, TransactionContext transaction) {
        return moved(delegate.insert(resource, amount, transaction), transaction);
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        return moved(delegate.extract(index, resource, amount, transaction), transaction);
    }

    @Override
    public int extract(T resource, int amount, TransactionContext transaction) {
        return moved(delegate.extract(resource, amount, transaction), transaction);
    }
}

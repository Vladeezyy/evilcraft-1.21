package org.cyclops.evilcraft.fabric.transfer;

import javax.annotation.Nullable;

/**
 * A NeoForge style transaction on top of a Fabric transaction.
 */
public final class Transaction implements AutoCloseable, TransactionContext {

    private final net.fabricmc.fabric.api.transfer.v1.transaction.Transaction delegate;

    private Transaction(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction delegate) {
        this.delegate = delegate;
    }

    /**
     * Opens a transaction; it is nested when another mod called us from a transaction of its own on this thread.
     */
    public static Transaction openRoot() {
        return new Transaction(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openNested(
                net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.getCurrentUnsafe()));
    }

    public static Transaction open(@Nullable TransactionContext parent) {
        return parent == null ? openRoot() : new Transaction(parent.fabric().openNested());
    }

    public void commit() {
        delegate.commit();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext fabric() {
        return delegate;
    }
}

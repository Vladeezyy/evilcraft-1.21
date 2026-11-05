package org.cyclops.evilcraft.fabric.transfer;

/**
 * An open transaction, backed by a Fabric transaction, so that transactions of this mod and other mods nest.
 */
public interface TransactionContext {

    net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext fabric();

    default int depth() {
        return fabric().nestingDepth();
    }

    static TransactionContext wrap(net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext context) {
        return () -> context;
    }
}

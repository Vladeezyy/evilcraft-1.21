package org.cyclops.evilcraft.fabric.transfer;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.CloseCallback;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext.Result;

import java.util.ArrayList;

/**
 * Keeps a state per open transaction, restores it on abort and reports the first one after the outer commit.
 * Fabric's SnapshotParticipant does not give the original state to its final commit callback.
 */
public abstract class SnapshotJournal<T> implements CloseCallback {

    private static final Object NONE = new Object();

    private final ArrayList<Object> snapshots = new ArrayList<>(2);

    protected abstract T createSnapshot();

    protected abstract void revertToSnapshot(T snapshot);

    protected void onRootCommit(T originalState) {
    }

    public final boolean isInTransaction() {
        return !snapshots.isEmpty();
    }

    public void updateSnapshots(TransactionContext transaction) {
        var context = transaction.fabric();
        int depth = context.nestingDepth();
        while (snapshots.size() <= depth) {
            snapshots.add(NONE);
        }
        if (snapshots.get(depth) == NONE) {
            snapshots.set(depth, createSnapshot());
            context.addCloseCallback(this);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onClose(net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext context, Result result) {
        int depth = context.nestingDepth();
        T snapshot = (T) snapshots.get(depth);
        snapshots.set(depth, NONE);
        trim();
        if (result.wasAborted()) {
            revertToSnapshot(snapshot);
        } else if (depth > 0) {
            // A state of the parent is older, so that one stays
            while (snapshots.size() < depth) {
                snapshots.add(NONE);
            }
            if (snapshots.get(depth - 1) == NONE) {
                snapshots.set(depth - 1, snapshot);
                context.getOpenTransaction(depth - 1).addCloseCallback(this);
            }
        } else {
            context.addOuterCloseCallback(ignored -> onRootCommit(snapshot));
        }
    }

    private void trim() {
        int last = snapshots.size() - 1;
        while (last >= 0 && snapshots.get(last) == NONE) {
            snapshots.remove(last--);
        }
    }
}

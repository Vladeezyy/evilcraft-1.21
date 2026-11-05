package org.cyclops.evilcraft.fabric.transfer;

/**
 * Runs a callback once the outer transaction has committed.
 */
public final class RootCommitJournal extends SnapshotJournal<Boolean> {

    private final Runnable callback;

    public RootCommitJournal(Runnable callback) {
        this.callback = callback;
    }

    @Override
    protected Boolean createSnapshot() {
        return Boolean.TRUE;
    }

    @Override
    protected void revertToSnapshot(Boolean snapshot) {
    }

    @Override
    protected void onRootCommit(Boolean originalState) {
        callback.run();
    }
}

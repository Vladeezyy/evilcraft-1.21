package org.cyclops.evilcraft.fabric.event;

/**
 * An event whose default outcome can be canceled.
 */
public abstract class CancellableEvent {

    private boolean canceled;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}

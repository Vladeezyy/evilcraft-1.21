package org.cyclops.evilcraft.fabric.extensions;

/**
 * A block entity that reacts to the data it received from the server (called through a mixin on the client).
 */
public interface IBlockEntityUpdateListener {
    void onUpdateReceived();
}

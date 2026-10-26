package org.cyclops.evilcraft.fabric.fluids.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * Conversions between millibuckets and Fabric droplets (81 per millibucket).
 */
public final class FluidAmounts {

    public static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000;

    private FluidAmounts() {
    }

    public static long toDroplets(int mb) {
        return mb * DROPLETS_PER_MB;
    }

    /**
     * Whole millibuckets in an amount of droplets, rounded down.
     */
    public static int toMb(long droplets) {
        return (int) Math.min(Integer.MAX_VALUE, droplets / DROPLETS_PER_MB);
    }

    /**
     * Opens a transaction that is nested in the transaction of this thread, if there is one.
     */
    public static Transaction open() {
        return Transaction.openNested(Transaction.getCurrentUnsafe());
    }
}

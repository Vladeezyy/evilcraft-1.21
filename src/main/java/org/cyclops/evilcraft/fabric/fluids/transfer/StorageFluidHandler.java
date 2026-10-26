package org.cyclops.evilcraft.fabric.fluids.transfer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;

/**
 * A Fabric fluid storage (of another mod or vanilla) seen as a fluid handler. Only whole millibuckets are moved.
 */
public class StorageFluidHandler implements IFluidHandler {

    protected final Storage<FluidVariant> storage;

    public StorageFluidHandler(Storage<FluidVariant> storage) {
        this.storage = storage;
    }

    protected void onCommit() {
    }

    private StorageView<FluidVariant> view(int tank) {
        int i = 0;
        for (StorageView<FluidVariant> view : storage) {
            if (i++ == tank) {
                return view;
            }
        }
        return null;
    }

    @Override
    public int getTanks() {
        int i = 0;
        for (StorageView<FluidVariant> ignored : storage) {
            i++;
        }
        return i;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        StorageView<FluidVariant> view = view(tank);
        if (view == null || view.isResourceBlank()) {
            return FluidStack.EMPTY;
        }
        return FluidStack.fromVariant(view.getResource(), FluidAmounts.toMb(view.getAmount()));
    }

    @Override
    public int getTankCapacity(int tank) {
        StorageView<FluidVariant> view = view(tank);
        return view == null ? 0 : FluidAmounts.toMb(view.getCapacity());
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !storage.supportsInsertion()) {
            return 0;
        }
        FluidVariant variant = resource.toVariant();
        long max = FluidAmounts.toDroplets(resource.getAmount());
        long inserted;
        try (Transaction transaction = FluidAmounts.open()) {
            inserted = storage.insert(variant, max, transaction);
        }
        // Only whole millibuckets
        int mb = FluidAmounts.toMb(inserted);
        if (mb <= 0 || action.simulate()) {
            return mb;
        }
        try (Transaction transaction = FluidAmounts.open()) {
            long done = storage.insert(variant, FluidAmounts.toDroplets(mb), transaction);
            if (done == FluidAmounts.toDroplets(mb)) {
                transaction.commit();
                onCommit();
                return mb;
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !storage.supportsExtraction()) {
            return FluidStack.EMPTY;
        }
        return drain(resource.toVariant(), resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0 || !storage.supportsExtraction()) {
            return FluidStack.EMPTY;
        }
        for (StorageView<FluidVariant> view : storage) {
            if (!view.isResourceBlank() && view.getAmount() > 0) {
                FluidStack drained = drain(view.getResource(), maxDrain, action);
                if (!drained.isEmpty()) {
                    return drained;
                }
            }
        }
        return FluidStack.EMPTY;
    }

    private FluidStack drain(FluidVariant variant, int maxMb, FluidAction action) {
        long extracted;
        try (Transaction transaction = FluidAmounts.open()) {
            extracted = storage.extract(variant, FluidAmounts.toDroplets(maxMb), transaction);
        }
        int mb = FluidAmounts.toMb(extracted);
        if (mb <= 0) {
            return FluidStack.EMPTY;
        }
        if (action.simulate()) {
            return FluidStack.fromVariant(variant, mb);
        }
        try (Transaction transaction = FluidAmounts.open()) {
            long done = storage.extract(variant, FluidAmounts.toDroplets(mb), transaction);
            if (done == FluidAmounts.toDroplets(mb)) {
                transaction.commit();
                onCommit();
                return FluidStack.fromVariant(variant, mb);
            }
        }
        return FluidStack.EMPTY;
    }
}

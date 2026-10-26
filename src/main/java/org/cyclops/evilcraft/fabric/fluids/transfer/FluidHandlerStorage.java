package org.cyclops.evilcraft.fabric.fluids.transfer;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.cyclops.evilcraft.fabric.cyclopscore.fluid.SingleUseTank;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;

import java.util.Iterator;
import java.util.List;

/**
 * Exposes an EvilCraft fluid handler as a Fabric fluid storage.
 * Changes are applied at once and undone if the transaction is aborted.
 */
public class FluidHandlerStorage implements Storage<FluidVariant> {

    private final IFluidHandler handler;

    public FluidHandlerStorage(IFluidHandler handler) {
        this.handler = handler;
    }

    public IFluidHandler getHandler() {
        return handler;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        int mb = FluidAmounts.toMb(maxAmount);
        if (resource.isBlank() || mb <= 0) {
            return 0;
        }
        FluidStack stack = FluidStack.fromVariant(resource, mb);
        if (handler.fill(stack, IFluidHandler.FluidAction.SIMULATE) <= 0) {
            return 0;
        }
        Runnable restore = restorer(stack.copyWithAmount(1), true);
        int filled = handler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
        if (filled > 0) {
            UndoCallback.register(transaction, restore != null ? restore
                    : () -> handler.drain(stack.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE));
        }
        return FluidAmounts.toDroplets(filled);
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        int mb = FluidAmounts.toMb(maxAmount);
        if (resource.isBlank() || mb <= 0) {
            return 0;
        }
        FluidStack request = FluidStack.fromVariant(resource, mb);
        if (handler.drain(request, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
            return 0;
        }
        Runnable restore = restorer(request, false);
        FluidStack drained = handler.drain(request, IFluidHandler.FluidAction.EXECUTE);
        if (!drained.isEmpty()) {
            FluidStack undo = drained.copy();
            UndoCallback.register(transaction, restore != null ? restore
                    : () -> handler.fill(undo, IFluidHandler.FluidAction.EXECUTE));
        }
        return FluidAmounts.toDroplets(drained.getAmount());
    }

    /**
     * For tanks, the exact previous state (also the fluid a single-use tank accepts), which a filling or draining in
     * the opposite direction would not give back.
     */
    @Nullable
    private Runnable restorer(FluidStack change, boolean fill) {
        if (handler instanceof FluidTank tank) {
            FluidStack before = tank.getFluid().copy();
            Fluid accepted = tank instanceof SingleUseTank singleUseTank ? singleUseTank.getAcceptedFluid() : null;
            return () -> {
                tank.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                if (accepted != null) {
                    ((SingleUseTank) tank).setAcceptedFluid(before.isEmpty() ? Fluids.EMPTY : before.getFluid());
                }
                if (!before.isEmpty()) {
                    tank.fill(before, IFluidHandler.FluidAction.EXECUTE);
                }
                if (accepted != null) {
                    ((SingleUseTank) tank).setAcceptedFluid(accepted);
                }
            };
        }
        return null;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        List<StorageView<FluidVariant>> views = Lists.newArrayListWithCapacity(handler.getTanks());
        for (int i = 0; i < handler.getTanks(); i++) {
            views.add(new TankView(i));
        }
        return views.iterator();
    }

    private class TankView implements StorageView<FluidVariant> {
        private final int tank;

        private TankView(int tank) {
            this.tank = tank;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
            FluidStack contained = handler.getFluidInTank(tank);
            if (contained.isEmpty() || !resource.equals(contained.toVariant())) {
                return 0;
            }
            return FluidHandlerStorage.this.extract(resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return handler.getFluidInTank(tank).isEmpty();
        }

        @Override
        public FluidVariant getResource() {
            return handler.getFluidInTank(tank).toVariant();
        }

        @Override
        public long getAmount() {
            return FluidAmounts.toDroplets(handler.getFluidInTank(tank).getAmount());
        }

        @Override
        public long getCapacity() {
            return FluidAmounts.toDroplets(handler.getTankCapacity(tank));
        }
    }
}

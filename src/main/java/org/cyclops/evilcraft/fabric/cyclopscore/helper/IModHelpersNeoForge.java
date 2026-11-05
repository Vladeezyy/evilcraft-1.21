package org.cyclops.evilcraft.fabric.cyclopscore.helper;

import org.cyclops.evilcraft.fabric.cyclopscore.datastructure.DimPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.cyclops.evilcraft.fabric.capabilities.BlockCapability;
import org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid.IFluidHandlerCapacity;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;
import org.cyclops.evilcraft.fabric.transfer.FluidUtil;
import org.cyclops.evilcraft.fabric.transfer.ItemAccess;
import org.cyclops.evilcraft.fabric.transfer.ResourceHandler;
import org.cyclops.evilcraft.fabric.transfer.Transaction;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * The helpers that only the NeoForge build of Cyclops Core has, on the transfer layer of this port.
 * Fluid amounts are millibuckets here; Cyclops Core's Fabric helpers count droplets.
 */
public final class IModHelpersNeoForge {

    private static final IModHelpersNeoForge INSTANCE = new IModHelpersNeoForge();
    private final FluidHelpers fluidHelpers = new FluidHelpers();
    private final CapabilityHelpers capabilityHelpers = new CapabilityHelpers();

    private IModHelpersNeoForge() {
    }

    public static IModHelpersNeoForge get() {
        return INSTANCE;
    }

    public FluidHelpers getFluidHelpers() {
        return fluidHelpers;
    }

    public CapabilityHelpers getCapabilityHelpers() {
        return capabilityHelpers;
    }

    public static final class FluidHelpers {

        public int getBucketVolume() {
            return 1000;
        }

        public int getAmount(FluidStack fluidStack) {
            return fluidStack.getAmount();
        }

        public FluidStack copy(FluidStack fluidStack) {
            return fluidStack.isEmpty() ? FluidStack.EMPTY : fluidStack.copy();
        }

        public FluidStack getFluid(@Nullable ResourceHandler<FluidResource> handler) {
            return handler != null && handler.size() > 0 ? FluidUtil.getStack(handler, 0) : FluidStack.EMPTY;
        }

        public boolean hasFluid(@Nullable ResourceHandler<FluidResource> handler) {
            return !getFluid(handler).isEmpty();
        }

        public long getCapacity(@Nullable ResourceHandler<FluidResource> handler) {
            long capacity = 0;
            if (handler != null) {
                for (int i = 0; i < handler.size(); i++) {
                    FluidResource resource = handler.getResource(i);
                    if (!resource.isEmpty()) {
                        capacity += handler.getCapacityAsLong(i, resource);
                    }
                }
            }
            return capacity;
        }

        public Optional<IFluidHandlerCapacity> getFluidHandlerItemCapacity(ItemAccess itemAccess) {
            return Optional.ofNullable(itemAccess.getCapability(org.cyclops.evilcraft.fabric.cyclopscore.Capabilities.Item.FLUID_HANDLER_CAPACITY));
        }

        public FluidStack move(ResourceHandler<FluidResource> source, ResourceHandler<FluidResource> destination, int maxAmount,
                               @Nullable Player player, boolean emptySound, boolean simulate) {
            FluidStack moved;
            try (Transaction transaction = Transaction.openRoot()) {
                moved = FluidUtil.moveFirst(source, destination, maxAmount, transaction);
                if (!simulate) {
                    transaction.commit();
                }
            }
            if (!simulate && player != null && !moved.isEmpty()) {
                FluidUtil.playSound(moved, player, null, !emptySound);
            }
            return moved;
        }
    }

    public static final class CapabilityHelpers {

        public <T, C> Optional<T> getCapability(DimPos dimPos, BlockCapability<T, C> capability) {
            return getCapability(dimPos, null, capability);
        }

        public <T, C> Optional<T> getCapability(DimPos dimPos, @Nullable C context, BlockCapability<T, C> capability) {
            return dimPos.getLevel(true) instanceof Level level
                    ? getCapability(level, dimPos.getBlockPos(), context, capability) : Optional.empty();
        }

        public <T, C> Optional<T> getCapability(Level level, BlockPos pos, BlockCapability<T, C> capability) {
            return getCapability(level, pos, null, capability);
        }

        public <T, C> Optional<T> getCapability(Level level, BlockPos pos, @Nullable C context, BlockCapability<T, C> capability) {
            return Optional.ofNullable(capability.getCapability(level, pos, context));
        }
    }
}

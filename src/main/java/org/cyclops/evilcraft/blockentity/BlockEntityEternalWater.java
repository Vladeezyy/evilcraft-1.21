package org.cyclops.evilcraft.blockentity;

import org.cyclops.evilcraft.fabric.cyclopscore.helper.IModHelpersNeoForge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.transfer.ResourceHandler;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;
import org.cyclops.evilcraft.fabric.transfer.Transaction;
import org.cyclops.evilcraft.fabric.transfer.TransactionContext;
import org.cyclops.cyclopscore.blockentity.BlockEntityTickerDelayed;
import org.cyclops.cyclopscore.blockentity.CyclopsBlockEntity;
import org.cyclops.evilcraft.fabric.cyclopscore.capability.registrar.BlockEntityCapabilityRegistrar;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.cyclopscore.persist.nbt.NBTPersist;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.block.BlockEternalWaterConfig;

import java.util.function.Supplier;

/**
 * Tile Entity for the eternal water blockState.
 * @author rubensworks
 *
 */
public class BlockEntityEternalWater extends CyclopsBlockEntity {

    public static final FluidStack WATER = new FluidStack(Fluids.WATER, Integer.MAX_VALUE);

    @NBTPersist
    private boolean enabled = BlockEternalWaterConfig.autoOutputDefault;

    public BlockEntityEternalWater(BlockPos blockPos, BlockState blockState) {
        super(RegistryEntries.BLOCK_ENTITY_ETERNAL_WATER.get(), blockPos, blockState);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        sendUpdate();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static class CapabilityRegistrar extends BlockEntityCapabilityRegistrar<BlockEntityEternalWater> {
        public CapabilityRegistrar(Supplier<BlockEntityType<? extends BlockEntityEternalWater>> blockEntityType) {
            super(blockEntityType);
        }

        @Override
        public void populate() {
            add(org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.BLOCK, (blockEntity, direction) -> new InfiniteWaterFluidCapability());
        }
    }

    public static class InfiniteWaterFluidCapability implements ResourceHandler<FluidResource> {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public FluidResource getResource(int tank) {
            return FluidResource.of(WATER);
        }

        @Override
        public long getAmountAsLong(int tank) {
            return Long.MAX_VALUE;
        }

        @Override
        public long getCapacityAsLong(int tank, FluidResource resource) {
            return Long.MAX_VALUE;
        }

        @Override
        public boolean isValid(int tank, FluidResource resource) {
            return false;
        }

        @Override
        public int insert(int tank, FluidResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public int extract(int tank, FluidResource resource, int amount, TransactionContext transaction) {
            return amount;
        }
    }

    public static class TickerServer extends BlockEntityTickerDelayed<BlockEntityEternalWater> {
        @Override
        protected void update(Level level, BlockPos pos, BlockState blockState, BlockEntityEternalWater blockEntity) {
            super.update(level, pos, blockState, blockEntity);

            if (blockEntity.isEnabled()) {
                for(Direction direction : Direction.values()) {
                    IModHelpersNeoForge.get().getCapabilityHelpers().getCapability(level, pos.relative(direction),
                                    direction.getOpposite(), org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.BLOCK)
                            .ifPresent(handler -> {
                                try (var tx = Transaction.openRoot()) {
                                    handler.insert(FluidResource.of(WATER), WATER.getAmount(), tx);
                                    tx.commit();
                                }
                            });
                }
            }
        }
    }
}

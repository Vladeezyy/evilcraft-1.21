package org.cyclops.evilcraft.fabric.capabilities;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * A block capability in the style of NeoForge, backed by a Fabric {@link BlockApiLookup}.
 * @param <T> The capability type.
 * @param <C> The context type (a side for sided capabilities).
 */
public final class BlockCapability<T, C> {

    private final BlockApiLookup<T, C> lookup;

    private BlockCapability(BlockApiLookup<T, C> lookup) {
        this.lookup = lookup;
    }

    public static <T> BlockCapability<T, Direction> createSided(Identifier name, Class<T> type) {
        return new BlockCapability<>(BlockApiLookup.get(name, type, Direction.class));
    }

    public BlockApiLookup<T, C> lookup() {
        return lookup;
    }

    @Nullable
    public T getCapability(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, C context) {
        return lookup.find(level, pos, state, blockEntity, context);
    }

    @Nullable
    public T getCapability(Level level, BlockPos pos, C context) {
        return lookup.find(level, pos, context);
    }
}

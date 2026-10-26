package org.cyclops.evilcraft.fabric.cyclopscore.block;

import org.cyclops.cyclopscore.block.BlockWithEntityCommon;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.cyclops.evilcraft.fabric.cyclopscore.blockentity.CyclopsBlockEntity;

import java.util.function.BiFunction;

/**
 * Base block with a block entity.
 *
 * By default, the NBT data of block entities will not be persisted,
 * unless enabled via {@link #isPersistNbt()}.
 * If so, then the {@link #getDroppedItemStackNbt} method will be called
 * to call {@link CyclopsBlockEntity#writeToItemStack(CompoundTag, HolderLookup.Provider)}.
 * This NBT data will automatically be read when placing the block.
 *
 * @author rubensworks
 */
@Deprecated // TODO: Use BlockWithEntityCommon instead; rm in next major
public abstract class BlockWithEntity extends BlockWithEntityCommon {
    public BlockWithEntity(Properties properties, BiFunction<BlockPos, BlockState, CyclopsBlockEntity> blockEntitySupplier) {
        super(properties, blockEntitySupplier);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos blockPos, BlockState state) {
        return BlockWithEntityCommon.getCloneItemStack(this, () -> super.getCloneItemStack(world, blockPos, state), state, null, world, blockPos, null);
    }
}

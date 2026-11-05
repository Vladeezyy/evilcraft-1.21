package org.cyclops.evilcraft.fabric.extensions;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

/**
 * Axes on the logs of the mod. Since 26.3 vanilla keeps stripping in one data file, block_transformer/axe.json, that a
 * mod can only replace as a whole, and Fabric API has no registry for it any more.
 */
public final class StrippableBlocks {

    private static final Map<Block, Block> STRIPPED = Maps.newIdentityHashMap();

    private StrippableBlocks() {
    }

    public static void add(Block block, Block stripped) {
        if (STRIPPED.isEmpty()) {
            UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
                ItemStack stack = player.getItemInHand(hand);
                if (!stack.is(ItemTags.AXES) || player.isSpectator()) {
                    return InteractionResult.PASS;
                }
                BlockPos pos = hit.getBlockPos();
                BlockState state = level.getBlockState(pos);
                Block to = STRIPPED.get(state.getBlock());
                if (to == null) {
                    return InteractionResult.PASS;
                }
                level.playSound(player, pos, SoundEvents.AXE_STRIP.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!level.isClientSide()) {
                    level.setBlock(pos, to.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)), Block.UPDATE_ALL_IMMEDIATE);
                    stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                }
                return InteractionResult.SUCCESS;
            });
        }
        STRIPPED.put(block, stripped);
    }
}

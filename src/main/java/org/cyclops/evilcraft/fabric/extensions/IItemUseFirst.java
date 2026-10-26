package org.cyclops.evilcraft.fabric.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelReader;

/**
 * An item that acts before the block it is used on.
 */
public interface IItemUseFirst {

    /**
     * Called before the block is used; anything but {@link InteractionResult#PASS} stops the interaction.
     */
    default InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return InteractionResult.PASS;
    }

    /**
     * @return If the block is still used when the player sneaks with this item.
     */
    default boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return false;
    }
}

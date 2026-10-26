package org.cyclops.evilcraft.fabric.extensions;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * An item that acts when a player drops it.
 */
public interface IItemDroppedByPlayer {
    /**
     * @return False to keep the item from being dropped.
     */
    boolean onDroppedByPlayer(ItemStack stack, Player player);
}

package org.cyclops.evilcraft.fabric.extensions;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

/**
 * An item that acts while it lies as an item entity in the world.
 */
public interface IItemEntityUpdate {
    /**
     * Called at the start of each tick of the item entity.
     * @return True to skip the rest of the tick.
     */
    boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity);
}

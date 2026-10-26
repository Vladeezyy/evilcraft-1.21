package org.cyclops.evilcraft.fabric.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * An item that can prevent the attack of an entity.
 */
public interface IItemLeftClickEntity {
    /**
     * @return True to cancel the attack.
     */
    boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity);
}

package org.cyclops.evilcraft.fabric.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * An item that replaces its dropped item entity by its own entity.
 */
public interface ICustomItemEntity {

    boolean hasCustomEntity(ItemStack itemStack);

    /**
     * @param location The plain item entity that is being added.
     * @return The entity to add instead, or null to keep the plain one.
     */
    @Nullable
    Entity createEntity(Level level, Entity location, ItemStack itemStack);
}

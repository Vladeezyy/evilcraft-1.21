package org.cyclops.evilcraft.fabric.capabilities;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * An item capability in the style of NeoForge, backed by a Fabric {@link ItemApiLookup}.
 * @param <T> The capability type.
 * @param <C> The context type ({@link Void} for none).
 */
public final class ItemCapability<T, C> {

    private final ItemApiLookup<T, C> lookup;

    private ItemCapability(ItemApiLookup<T, C> lookup) {
        this.lookup = lookup;
    }

    public static <T> ItemCapability<T, Void> createVoid(ResourceLocation name, Class<T> type) {
        return new ItemCapability<>(ItemApiLookup.get(name, type, Void.class));
    }

    public ItemApiLookup<T, C> lookup() {
        return lookup;
    }

    @Nullable
    public T getCapability(ItemStack stack, @Nullable C context) {
        if (stack.isEmpty()) {
            return null;
        }
        return lookup.find(stack, context);
    }
}

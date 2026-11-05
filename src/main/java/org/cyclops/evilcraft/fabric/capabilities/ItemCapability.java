package org.cyclops.evilcraft.fabric.capabilities;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * An item capability in the style of NeoForge, backed by a Fabric {@link ItemApiLookup}.
 * @param <T> The capability type.
 * @param <C> The context type.
 */
public final class ItemCapability<T, C> {

    private final ItemApiLookup<T, C> lookup;

    private ItemCapability(ItemApiLookup<T, C> lookup) {
        this.lookup = lookup;
    }

    public static <T, C> ItemCapability<T, C> create(Identifier name, Class<T> type, Class<C> contextType) {
        return new ItemCapability<>(ItemApiLookup.get(name, type, contextType));
    }

    public ItemApiLookup<T, C> lookup() {
        return lookup;
    }

    @Nullable
    public T getCapability(ItemStack stack, @Nullable C context) {
        return stack.isEmpty() ? null : lookup.find(stack, context);
    }
}

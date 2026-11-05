package org.cyclops.evilcraft.fabric.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.cyclops.cyclopscore.config.DeferredHolderCommon;

import java.util.function.Supplier;

/**
 * A holder of a registry entry that is resolved on first use.
 */
public class DeferredHolder<R, T extends R> extends DeferredHolderCommon<R, T> implements Supplier<T>, ItemLike {

    protected DeferredHolder(ResourceKey<R> key) {
        super(key);
    }

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<? extends Registry<R>> registryKey, Identifier valueName) {
        return new DeferredHolder<>(ResourceKey.create(registryKey, valueName));
    }

    /**
     * Since 26.2 Holder is sealed, so a holder of an item can only be passed to vanilla as an item; other holders through
     * {@link #getHolder()}.
     */
    @Override
    public Item asItem() {
        return ((ItemLike) value()).asItem();
    }

    @Override
    public T get() {
        return value();
    }

    public Identifier getId() {
        return key.identifier();
    }

    public ResourceKey<R> getKey() {
        return key;
    }
}

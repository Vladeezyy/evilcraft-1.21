package org.cyclops.evilcraft.fabric.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.cyclops.cyclopscore.config.DeferredHolderCommon;

import java.util.function.Supplier;

/**
 * A holder of a registry entry that is resolved on first use.
 */
public class DeferredHolder<R, T extends R> extends DeferredHolderCommon<R, T> implements Supplier<T> {

    protected DeferredHolder(ResourceKey<R> key) {
        super(key);
    }

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<? extends Registry<R>> registryKey, Identifier valueName) {
        return new DeferredHolder<>(ResourceKey.create(registryKey, valueName));
    }

    @Override
    public T get() {
        return value();
    }

    public Identifier getId() {
        return key.location();
    }

    public ResourceKey<R> getKey() {
        return key;
    }
}

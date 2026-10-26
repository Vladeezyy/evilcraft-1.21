package org.cyclops.evilcraft.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import javax.annotation.Nullable;

/**
 * Client side parts of {@link CommonHooks}.
 */
@Environment(EnvType.CLIENT)
public final class ClientHooks {

    private ClientHooks() {
    }

    @Nullable
    public static <T> HolderLookup.RegistryLookup<T> resolveLookup(ResourceKey<? extends Registry<T>> key) {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? null : minecraft.level.registryAccess().lookup(key).orElse(null);
    }
}

package org.cyclops.evilcraft.fabric;

import net.minecraft.server.MinecraftServer;
import org.cyclops.cyclopscore.helper.IModHelpers;

import javax.annotation.Nullable;

/**
 * Access to the running server.
 */
public final class ServerLifecycleHooks {

    private ServerLifecycleHooks() {
    }

    @Nullable
    public static MinecraftServer getCurrentServer() {
        return IModHelpers.get().getMinecraftHelpers().getCurrentServer();
    }
}

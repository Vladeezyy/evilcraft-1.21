package org.cyclops.evilcraft.scene;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Runs {@link DevScene} after each client tick when the evilcraft.scene system property is set.
 */
public class SceneHooks implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (System.getProperty("evilcraft.perf") != null) {
            PerfScene.register();
        } else if (System.getProperty("evilcraft.scene") != null) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> DevScene.onTick());
        }
    }
}

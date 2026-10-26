package org.cyclops.evilcraft.fabric.event;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Runs code once all registries of the mod are filled (end of mod initialization), like the registration events of
 * NeoForge's mod bus.
 */
public final class ModLifecycle {

    private static final List<Runnable> AFTER_REGISTRIES = Lists.newArrayList();
    private static boolean registriesFilled = false;

    private ModLifecycle() {
    }

    public static synchronized void afterRegistries(Runnable runnable) {
        if (registriesFilled) {
            runnable.run();
        } else {
            AFTER_REGISTRIES.add(runnable);
        }
    }

    public static void fireAfterRegistries() {
        List<Runnable> runnables;
        synchronized (ModLifecycle.class) {
            registriesFilled = true;
            runnables = Lists.newArrayList(AFTER_REGISTRIES);
            AFTER_REGISTRIES.clear();
        }
        runnables.forEach(Runnable::run);
    }
}

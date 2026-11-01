package org.cyclops.evilcraftcompat;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.cyclops.evilcraftcompat.modcompat.jei.JeiCompatLoader;
import org.cyclops.evilcraftcompat.modcompat.jei.JeiCompatLoaderClient;

/**
 * Fabric: EvilCraft-Compat is part of EvilCraft, as in the original (which bundles it into its jar).
 * The JEI and Jade plugins are entrypoints of fabric.mod.json, called only when those mods are present.
 * @author rubensworks
 */
public class EvilCraftCompat {

    public static void load() {
        JeiCompatLoader.initialize();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && FabricLoader.getInstance().isModLoaded(Reference.MOD_JEI)) {
            JeiCompatLoaderClient.initialize();
        }
    }
}

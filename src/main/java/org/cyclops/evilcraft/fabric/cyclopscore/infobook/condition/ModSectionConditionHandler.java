package org.cyclops.evilcraft.fabric.cyclopscore.infobook.condition;

import net.fabricmc.loader.api.FabricLoader;


import net.neoforged.fml.ModList;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;

/**
 * Condition handler for checking if mods are available.
 * @author rubensworks
 *
 */
public class ModSectionConditionHandler implements ISectionConditionHandler {

    @Override
    public boolean isSatisfied(ModBaseNeoForge<?> mod, String param) {
        return ModList.get().isLoaded(param);
    }

}

package org.cyclops.evilcraft.fabric.cyclopscore.infobook.condition;

import net.fabricmc.loader.api.FabricLoader;


import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

/**
 * Condition handler for checking if mods are available.
 * @author rubensworks
 *
 */
public class ModSectionConditionHandler implements ISectionConditionHandler {

    @Override
    public boolean isSatisfied(ModBase<?> mod, String param) {
        return FabricLoader.getInstance().isModLoaded(param);
    }

}

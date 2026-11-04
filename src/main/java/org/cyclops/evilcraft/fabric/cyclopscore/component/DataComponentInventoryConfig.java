package org.cyclops.evilcraft.fabric.cyclopscore.component;

import org.cyclops.evilcraft.EvilCraft;


import org.cyclops.cyclopscore.config.extendedconfig.DataComponentConfigCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.cyclopscore.inventory.SimpleInventory;

/**
 * @author rubensworks
 */
public class DataComponentInventoryConfig extends DataComponentConfigCommon<SimpleInventory, ModBaseNeoForge<?>> {
    public DataComponentInventoryConfig() {
        super(EvilCraft._instance, "inventory", builder -> builder
                .persistent(SimpleInventory.CODEC)
                .networkSynchronized(SimpleInventory.STREAM_CODEC));
    }
}

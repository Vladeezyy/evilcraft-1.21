package org.cyclops.evilcraft.fabric.cyclopscore.component;

import org.cyclops.evilcraft.EvilCraft;


import org.cyclops.evilcraft.fabric.fluids.SimpleFluidContent;
import org.cyclops.evilcraft.fabric.cyclopscore.CyclopsCoreNeoForge;
import org.cyclops.cyclopscore.config.extendedconfig.DataComponentConfigCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;

/**
 * @author rubensworks
 */
public class DataComponentFluidContentConfig extends DataComponentConfigCommon<SimpleFluidContent, ModBaseNeoForge<?>> {
    public DataComponentFluidContentConfig() {
        super(CyclopsCoreNeoForge._instance, "fluid_content", builder -> builder
                .persistent(SimpleFluidContent.CODEC)
                .networkSynchronized(SimpleFluidContent.STREAM_CODEC));
    }
}

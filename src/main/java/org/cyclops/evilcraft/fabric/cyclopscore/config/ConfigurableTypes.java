package org.cyclops.evilcraft.fabric.cyclopscore.config;

import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction.FluidActionFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction.LootModifierActionFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.FluidConfigNeoForge;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.LootModifierConfigNeoForge;

/**
 * The configurable types that Cyclops Core only has on NeoForge.
 * @author rubensworks
 */
public class ConfigurableTypes {

    public static final ConfigurableTypeCommon FLUID = new ConfigurableTypeCommon(true, FluidConfigNeoForge.class, new FluidActionFabric(), "fluid");
    public static final ConfigurableTypeCommon LOOT_MODIFIER = new ConfigurableTypeCommon(true, LootModifierConfigNeoForge.class, new LootModifierActionFabric<>(), "loot_modifier");

}

package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.Function;

/**
 * Config for villagers.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public abstract class VillagerConfig extends ExtendedConfigForge<VillagerConfig, VillagerProfession> {

    /**
     * Make a new instance.
     * @param mod     The mod instance.
     * @param namedId The unique name ID for the configurable.
     * @param elementConstructor The element constructor.
     */
    public VillagerConfig(ModBase<?> mod, String namedId, Function<VillagerConfig, ? extends VillagerProfession> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "entity.villager." + getNamedId();
    }

    @Override
    public String getFullTranslationKey() {
        return "entity.Villager." + getNamedId();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.VILLAGER;
    }

    @Override
    public Registry<? super VillagerProfession> getRegistry() {
        return BuiltInRegistries.VILLAGER_PROFESSION;
    }
}

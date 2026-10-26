package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.Function;

/**
 * Config for world features.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public abstract class WorldFeatureConfig extends ExtendedConfigForge<WorldFeatureConfig, Feature<?>> {
    public WorldFeatureConfig(ModBase<?> mod, String namedId, Function<WorldFeatureConfig, ? extends Feature<?>> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "features." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.WORLD_FEATURE;
    }

    @Override
    public Registry<? super Feature<?>> getRegistry() {
        return BuiltInRegistries.FEATURE;
    }
}

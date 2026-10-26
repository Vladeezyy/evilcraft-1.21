package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;


import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.UnaryOperator;

/**
 * Config for data component types.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public abstract class DataComponentConfig<T> extends ExtendedConfigForge<DataComponentConfig<T>, DataComponentType<T>> {
    public DataComponentConfig(ModBase<?> mod, String namedId, UnaryOperator<DataComponentType.Builder<T>> builder) {
        super(mod, namedId, (eConfig) -> builder.apply(DataComponentType.builder()).build());
    }

    @Override
    public String getTranslationKey() {
        return "datacomponent." + getMod().getModId() + "." + getNamedId();
    }

    // Needed for config gui
    @Override
    public String getFullTranslationKey() {
        return getTranslationKey();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.DATA_COMPONENT;
    }

    @Override
    public Registry<DataComponentType<?>> getRegistry() {
        return BuiltInRegistries.DATA_COMPONENT_TYPE;
    }
}

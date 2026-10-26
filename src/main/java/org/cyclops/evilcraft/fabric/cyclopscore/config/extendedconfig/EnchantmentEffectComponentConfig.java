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
 * Config for enchantment effect components.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public class EnchantmentEffectComponentConfig<T> extends ExtendedConfigForge<EnchantmentEffectComponentConfig<T>, DataComponentType<T>>{
    public EnchantmentEffectComponentConfig(ModBase<?> mod, String namedId, UnaryOperator<DataComponentType.Builder<T>> builder) {
        super(mod, namedId, (eConfig) -> builder.apply(DataComponentType.builder()).build());
    }

    @Override
    public String getTranslationKey() {
        return "enchantmenteffectcomponent." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.ENCHANTMENT_EFFECT_COMPONENT;
    }

    @Override
    public Registry<DataComponentType<?>> getRegistry() {
        return BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE;
    }
}

package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;


import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.Function;

/**
 * Config for enchantment entity effects.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public class EnchantmentEntityEffectConfig extends ExtendedConfigForge<EnchantmentEntityEffectConfig, MapCodec<? extends EnchantmentEntityEffect>>{
    public EnchantmentEntityEffectConfig(ModBase<?> mod, String namedId, Function<EnchantmentEntityEffectConfig, MapCodec<? extends EnchantmentEntityEffect>> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "enchantmententityeffect." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.ENCHANTMENT_ENTITY_EFFECT;
    }

    @Override
    public Registry<MapCodec<? extends EnchantmentEntityEffect>> getRegistry() {
        return BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE;
    }
}

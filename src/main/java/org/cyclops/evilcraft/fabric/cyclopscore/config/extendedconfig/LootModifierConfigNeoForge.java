package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import com.mojang.serialization.MapCodec;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.evilcraft.fabric.loot.IGlobalLootModifier;

import java.util.function.Function;

/**
 * Config for loot modifier types.
 * @author rubensworks
 */
public abstract class LootModifierConfigNeoForge<T extends IGlobalLootModifier> extends ExtendedConfigCommon<LootModifierConfigNeoForge<T>, MapCodec<T>, ModBaseNeoForge<?>> {

    public LootModifierConfigNeoForge(ModBaseNeoForge<?> mod, String namedId, Function<LootModifierConfigNeoForge<T>, MapCodec<T>> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "biomemodifier." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.LOOT_MODIFIER;
    }
}

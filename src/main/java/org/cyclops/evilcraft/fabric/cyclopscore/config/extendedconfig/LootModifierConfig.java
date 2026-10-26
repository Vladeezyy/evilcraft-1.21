package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import com.mojang.serialization.MapCodec;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;
import org.cyclops.evilcraft.fabric.loot.IGlobalLootModifier;

import java.util.function.Function;

/**
 * Config for loot modifier types.
 * @author rubensworks
 */
public abstract class LootModifierConfig<T extends IGlobalLootModifier> extends ExtendedConfig<LootModifierConfig<T>, MapCodec<T>> {

    public LootModifierConfig(ModBase<?> mod, String namedId, Function<LootModifierConfig<T>, MapCodec<T>> elementConstructor) {
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

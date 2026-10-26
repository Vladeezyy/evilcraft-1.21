package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.Function;

/**
 * Config for sound events.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public abstract class SoundEventConfig extends ExtendedConfigForge<SoundEventConfig, SoundEvent>{
    public SoundEventConfig(ModBase<?> mod, String namedId, Function<SoundEventConfig, SoundEvent> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "sound_events." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.SOUND_EVENT;
    }

    @Override
    public Registry<? super SoundEvent> getRegistry() {
        return BuiltInRegistries.SOUND_EVENT;
    }
}

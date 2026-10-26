package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;


import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Config for recipe serializers.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public abstract class ParticleConfig<T extends ParticleOptions> extends ExtendedConfigForge<ParticleConfig<T>, ParticleType<T>> {

    /**
     * Create a new config
     *
     * @param mod                The mod instance.
     * @param namedId            A unique name id
     * @param elementConstructor The element constructor.
     */
    public ParticleConfig(ModBase<?> mod, String namedId, Function<ParticleConfig<T>, ? extends ParticleType<T>> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "gui." + getMod().getModId() + "." + getNamedId();
    }

    // Needed for config gui
    @Override
    public String getFullTranslationKey() {
        return getTranslationKey();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.PARTICLE;
    }

    @Override
    public Registry<? super ParticleType<T>> getRegistry() {
        return BuiltInRegistries.PARTICLE_TYPE;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public abstract ParticleProvider<T> getParticleFactory();

    @Environment(EnvType.CLIENT)
    @Nullable
    public abstract ParticleEngine.SpriteParticleRegistration<T> getParticleMetaFactory();


}

package org.cyclops.evilcraft.fabric.cyclopscore.config.configurabletypeaction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.cyclops.cyclopscore.config.configurabletypeaction.ConfigurableTypeActionRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ParticleConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

/**
 * Registers particle types and, on the client, their providers.
 * @author rubensworks
 */
public class ParticleActionFabric<T extends ParticleOptions> extends ConfigurableTypeActionRegistry<ParticleConfig<T>, ParticleType<T>, ModBase<?>> {

    @Override
    public void onRegisterForgeFilled(ParticleConfig<T> eConfig) {
        register(eConfig, () -> {
            eConfig.onForgeRegistered();
            if (eConfig.getMod().getModHelpers().getMinecraftHelpers().isClientSide()) {
                registerClient(eConfig);
            }
            return null;
        });
    }

    @Environment(EnvType.CLIENT)
    protected static <T extends ParticleOptions> void registerClient(ParticleConfig<T> eConfig) {
        ParticleProvider<T> factory = eConfig.getParticleFactory();
        if (factory != null) {
            ParticleFactoryRegistry.getInstance().register(eConfig.getInstance(), factory);
        }
        ParticleEngine.SpriteParticleRegistration<T> metaFactory = eConfig.getParticleMetaFactory();
        if (metaFactory != null) {
            ParticleFactoryRegistry.getInstance().register(eConfig.getInstance(), metaFactory::create);
        }
    }
}

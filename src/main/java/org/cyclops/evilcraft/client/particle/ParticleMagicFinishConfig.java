package org.cyclops.evilcraft.client.particle;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ParticleConfig;
import org.cyclops.evilcraft.EvilCraft;

import javax.annotation.Nullable;

/**
 * Config for {@link ParticleMagicFinish}.
 * @author rubensworks
 */
public class ParticleMagicFinishConfig extends ParticleConfig<SimpleParticleType> {

    public ParticleMagicFinishConfig() {
        super(EvilCraft._instance, "magic_finish", eConfig -> new SimpleParticleType(false));
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    @Override
    public ParticleProvider<SimpleParticleType> getParticleFactory() {
        return null;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    @Override
    public ParticleEngine.SpriteParticleRegistration<SimpleParticleType> getParticleMetaFactory() {
        return sprite -> (ParticleProvider<SimpleParticleType>) (typeIn, worldIn, x, y, z, xSpeed, ySpeed, zSpeed) -> {
            ParticleMagicFinish particle = new ParticleMagicFinish(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(sprite);
            return particle;
        };
    }

}

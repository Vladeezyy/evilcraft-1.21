package org.cyclops.evilcraft.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ParticleConfig;
import org.cyclops.evilcraft.EvilCraft;

import javax.annotation.Nullable;

/**
 * Config for {@link ParticleExplosionExtended}.
 * @author rubensworks
 */
public class ParticleExplosionExtendedConfig extends ParticleConfig<ParticleExplosionExtendedData> {

    public ParticleExplosionExtendedConfig() {
        super(EvilCraft._instance, "explosion_extended", eConfig -> new ParticleType<ParticleExplosionExtendedData>(false) {

            @Override
            public MapCodec<ParticleExplosionExtendedData> codec() {
                return ParticleExplosionExtendedData.CODEC;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, ParticleExplosionExtendedData> streamCodec() {
                return ParticleExplosionExtendedData.STREAM_CODEC;
            }
        });
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    @Override
    public ParticleProvider<ParticleExplosionExtendedData> getParticleFactory() {
        return null;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    @Override
    public ParticleEngine.SpriteParticleRegistration<ParticleExplosionExtendedData> getParticleMetaFactory() {
        return sprite -> (ParticleProvider<ParticleExplosionExtendedData>) (particleData, worldIn, x, y, z, xSpeed, ySpeed, zSpeed) -> {
            ParticleExplosionExtended particle = new ParticleExplosionExtended(worldIn, x, y, z, xSpeed, ySpeed, zSpeed,
                    particleData.getR(), particleData.getG(), particleData.getB(), particleData.getAlpha(), sprite);
            particle.pickSprite(sprite);
            return particle;
        };
    }

}

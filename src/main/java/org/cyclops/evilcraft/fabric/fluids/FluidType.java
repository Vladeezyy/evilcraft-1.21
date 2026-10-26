package org.cyclops.evilcraft.fabric.fluids;

import com.google.common.collect.Maps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Properties of a fluid that are not part of vanilla's fluid class: used for Fabric's fluid variant attributes.
 */
public class FluidType {

    private final Properties properties;

    public FluidType(Properties properties) {
        this.properties = properties;
    }

    public String getDescriptionId() {
        return properties.descriptionId;
    }

    public int getTemperature() {
        return properties.temperature;
    }

    public int getViscosity() {
        return properties.viscosity;
    }

    public int getDensity() {
        return properties.density;
    }

    public int getLightLevel() {
        return properties.lightLevel;
    }

    public Rarity getRarity() {
        return properties.rarity;
    }

    public boolean canHydrate() {
        return properties.canHydrate;
    }

    @Nullable
    public SoundEvent getSound(SoundActions action) {
        return properties.sounds.get(action);
    }

    public static class Properties {
        private String descriptionId = "fluid.unknown";
        private int density = 1000;
        private int viscosity = 1000;
        private int temperature = 300;
        private int lightLevel = 0;
        private Rarity rarity = Rarity.COMMON;
        private boolean canHydrate = false;
        private boolean supportsBoating = false;
        private final Map<SoundActions, SoundEvent> sounds = Maps.newEnumMap(SoundActions.class);

        public static Properties create() {
            return new Properties();
        }

        public Properties descriptionId(String descriptionId) {
            this.descriptionId = descriptionId;
            return this;
        }

        public Properties density(int density) {
            this.density = density;
            return this;
        }

        public Properties viscosity(int viscosity) {
            this.viscosity = viscosity;
            return this;
        }

        public Properties temperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public Properties lightLevel(int lightLevel) {
            this.lightLevel = lightLevel;
            return this;
        }

        public Properties rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Properties canHydrate(boolean canHydrate) {
            this.canHydrate = canHydrate;
            return this;
        }

        public Properties supportsBoating(boolean supportsBoating) {
            this.supportsBoating = supportsBoating;
            return this;
        }

        public Properties sound(SoundActions action, SoundEvent sound) {
            this.sounds.put(action, sound);
            return this;
        }
    }
}

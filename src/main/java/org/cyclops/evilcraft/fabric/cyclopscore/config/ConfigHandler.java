package org.cyclops.evilcraft.fabric.cyclopscore.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.cyclopscore.config.ConfigHandlerFabric;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.FluidConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * The config handler of the mod.
 * @author rubensworks
 */
public class ConfigHandler extends ConfigHandlerFabric {
    public ConfigHandler(ModBase<?> mod) {
        super(mod);
    }

    @Override
    public void addToConfigDictionary(ExtendedConfigCommon<?, ?, ?> e) {
        if (e instanceof BlockConfig || e instanceof ItemConfig) {
            getDictionary().put(e.getNamedId(), e);
        }
        super.addToConfigDictionary(e);
    }

    /**
     * Register in the order of NeoForge's registration events (attributes, data components and armor materials first,
     * then vanilla's registry order) instead of the order in which the configs were added: entries of this mod depend
     * on entries of earlier registries at construction (spawn eggs on entity types, items on data components, ...).
     */
    @Override
    public void loadForgeRegistriesFilled() {
        Map<ResourceLocation, Integer> ranks = Maps.newHashMap();
        int rank = 0;
        for (ResourceLocation first : List.of(Registries.ATTRIBUTE.location(), Registries.DATA_COMPONENT_TYPE.location(), Registries.ARMOR_MATERIAL.location())) {
            ranks.put(first, rank++);
        }
        for (ResourceLocation registry : BuiltInRegistries.REGISTRY.keySet()) {
            if (!ranks.containsKey(registry)) {
                ranks.put(registry, rank++);
            }
        }
        int last = rank;
        List<ExtendedConfigCommon<?, ?, ?>> sorted = Lists.newArrayList(getConfigurables());
        sorted.sort(Comparator.comparingInt(config -> {
            if (config instanceof FluidConfig) {
                return ranks.get(Registries.FLUID.location());
            }
            if (config instanceof ExtendedConfigRegistry<?, ?, ?> registryConfig) {
                Registry<?> registry = registryConfig.getRegistry();
                if (registry != null) {
                    return ranks.getOrDefault(registry.key().location(), last);
                }
            }
            return last;
        }));
        for (ExtendedConfigCommon<?, ?, ?> eConfig : sorted) {
            eConfig.getConfigurableType().getConfigurableTypeAction().onRegisterForgeFilled(eConfig);
        }
    }
}

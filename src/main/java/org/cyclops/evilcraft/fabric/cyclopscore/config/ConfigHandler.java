package org.cyclops.evilcraft.fabric.cyclopscore.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.cyclops.cyclopscore.config.ConfigHandlerFabric;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigRegistry;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.FluidConfigNeoForge;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * The config handler of the mod: registers in the order of NeoForge's registration events, which the configs of
 * EvilCraft are written against.
 * @author rubensworks
 */
public class ConfigHandler extends ConfigHandlerFabric {

    private final List<Runnable> pendingItems = Lists.newArrayList();
    private boolean itemsOpen;
    private boolean registriesFilled;
    private boolean setupPending;

    public ConfigHandler(IModBase mod) {
        super(mod);
    }

    /**
     * Cyclops Core on Fabric runs the setup (and {@link ExtendedConfigCommon#onRegistered()}) before registering the
     * entries; on NeoForge the setup comes after the registries are filled, which configs rely on (block entity
     * renderers get their block entity type), so it is postponed until then.
     */
    @Override
    public void loadSetup() {
        if (!registriesFilled) {
            setupPending = true;
            return;
        }
        super.loadSetup();
    }

    /**
     * Block items wait for all blocks, like the item registration event of NeoForge: a block item can depend on another
     * block (wall torches).
     */
    @Override
    public <V> void registerToRegistry(Registry<? super V> registry, ExtendedConfigRegistry<?, V, ?> config, @Nullable Callable<?> callback) {
        if (registry == BuiltInRegistries.ITEM && !itemsOpen) {
            pendingItems.add(() -> super.registerToRegistry(registry, config, callback));
            return;
        }
        super.registerToRegistry(registry, config, callback);
    }

    /**
     * Register in the order of NeoForge's registration events (attributes and data components first, then vanilla's
     * registry order) instead of the order in which the configs were added: entries of this mod depend on entries of
     * earlier registries at construction (spawn eggs on entity types, items on data components, ...).
     */
    @Override
    public void loadRegistriesFilled() {
        Map<ResourceKey<?>, Integer> ranks = Maps.newHashMap();
        int rank = 0;
        for (ResourceKey<?> first : List.of(Registries.ATTRIBUTE, Registries.DATA_COMPONENT_TYPE)) {
            ranks.put(first, rank++);
        }
        // In the order in which the registries were created (the key set of the root registry is not ordered)
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            if (!ranks.containsKey(registry.key())) {
                ranks.put(registry.key(), rank++);
            }
        }
        int last = rank;
        int itemRank = ranks.get(Registries.ITEM);
        List<ExtendedConfigCommon<?, ?, ?>> sorted = Lists.newArrayList(getConfigurables());
        sorted.sort(Comparator.comparingInt(config -> rank(config, ranks, last)));
        for (ExtendedConfigCommon<?, ?, ?> eConfig : sorted) {
            if (!itemsOpen && rank(eConfig, ranks, last) >= itemRank) {
                openItems();
            }
            eConfig.getConfigurableType().getConfigurableTypeAction().onRegistriesFilled(eConfig);
        }
        openItems();
        registriesFilled = true;
        if (setupPending) {
            setupPending = false;
            super.loadSetup();
        }
    }

    private void openItems() {
        itemsOpen = true;
        List<Runnable> pending = Lists.newArrayList(pendingItems);
        pendingItems.clear();
        pending.forEach(Runnable::run);
    }

    private static int rank(ExtendedConfigCommon<?, ?, ?> config, Map<ResourceKey<?>, Integer> ranks, int last) {
        if (config instanceof FluidConfigNeoForge) {
            return ranks.get(Registries.FLUID);
        }
        if (config instanceof ExtendedConfigRegistry<?, ?, ?> registryConfig) {
            Registry<?> registry = registryConfig.getRegistry();
            if (registry != null) {
                return ranks.getOrDefault(registry.key(), last);
            }
        }
        return last;
    }
}

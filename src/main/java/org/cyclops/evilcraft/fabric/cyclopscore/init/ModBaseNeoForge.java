package org.cyclops.evilcraft.fabric.cyclopscore.init;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.server.MinecraftServer;
import org.cyclops.cyclopscore.CyclopsCoreFabric;
import org.cyclops.cyclopscore.helper.CyclopsCoreInstance;
import org.cyclops.cyclopscore.helper.ModBaseCommon;
import org.cyclops.cyclopscore.init.ModBaseFabric;
import org.cyclops.cyclopscore.proxy.ICommonProxyCommon;
import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.KeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigHandler;
import org.cyclops.evilcraft.fabric.cyclopscore.persist.world.WorldStorage;
import org.cyclops.evilcraft.fabric.cyclopscore.proxy.ICommonProxy;
import org.cyclops.evilcraft.fabric.event.ModLifecycle;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Base class of the mod on Fabric, on top of Cyclops Core's {@link ModBaseFabric}, with the parts of its NeoForge mod
 * base that EvilCraft uses. It keeps the name of the NeoForge class so the merges of upstream stay small.
 * @author rubensworks
 */
public abstract class ModBaseNeoForge<T extends ModBaseNeoForge<T>> extends ModBaseFabric<T> {

    public static final EnumReferenceKey<String> REFKEY_TEXTURE_PATH_GUI = EnumReferenceKey.create("texture_path_gui", String.class);
    public static final EnumReferenceKey<String> REFKEY_TEXTURE_PATH_MODELS = EnumReferenceKey.create("texture_path_models", String.class);
    public static final EnumReferenceKey<String> REFKEY_TEXTURE_PATH_SKINS = EnumReferenceKey.create("texture_path_skins", String.class);
    public static final EnumReferenceKey<Boolean> REFKEY_RETROGEN = EnumReferenceKey.create("retrogen", Boolean.class);
    public static final EnumReferenceKey<Boolean> REFKEY_CRASH_ON_INVALID_RECIPE = EnumReferenceKey.create("crash_on_invalid_recipe", Boolean.class);
    public static final EnumReferenceKey<Boolean> REFKEY_CRASH_ON_MODCOMPAT_CRASH = EnumReferenceKey.create("crash_on_modcompat_crash", Boolean.class);
    public static final EnumReferenceKey<Boolean> REFKEY_INFOBOOK_REWARDS = EnumReferenceKey.create("rewards", Boolean.class);

    // Not final and created on first use: the Fabric mod base registers the configs from its constructor,
    // before the fields of this class are initialized.
    private Map<EnumReferenceKey<?>, Object> genericReference;
    private List<WorldStorage.Access<?>> worldStorages;
    private IKeyRegistry keyRegistry;
    private ModContainer container;

    public ModBaseNeoForge(String modId, Consumer<T> instanceSetter) {
        super(constructCyclopsCore(modId), instanceSetter);
    }

    /**
     * Fabric does not run the entrypoints of dependencies first (in dev this mod comes before Cyclops Core), while this
     * mod uses Cyclops Core from its constructor on: create Cyclops Core's entrypoint first (Fabric caches the instance).
     */
    private static String constructCyclopsCore(String modId) {
        if (CyclopsCoreInstance.MOD == null) {
            for (EntrypointContainer<ModInitializer> container : FabricLoader.getInstance().getEntrypointContainers("main", ModInitializer.class)) {
                if (container.getProvider().getMetadata().getId().equals("cyclopscore")) {
                    container.getEntrypoint();
                }
            }
        }
        return modId;
    }

    @Override
    protected ConfigHandler constructConfigHandler() {
        return new ConfigHandler(this);
    }

    @Override
    public ConfigHandler getConfigHandler() {
        return (ConfigHandler) super.getConfigHandler();
    }

    @Override
    public void onInitialize() {
        // Cyclops Core registers its own data components and hooks there; its second call is skipped (CyclopsCoreFabricMixin)
        if (CyclopsCoreFabric._instance != null && !CyclopsCoreFabric._instance.isLoaded()) {
            CyclopsCoreFabric._instance.onInitialize();
        }
        super.onInitialize();

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);

        if (getModHelpers().getMinecraftHelpers().isClientSide() && getProxy() instanceof ICommonProxy commonProxy) {
            commonProxy.registerKeyBindings(getKeyRegistry());
        }

        // Everything is registered now
        RegisterCapabilitiesEvent.fire();
        ModLifecycle.fireAfterRegistries();
        setup();
    }

    /**
     * Called at the end of mod initialization, when all registries of this mod are filled.
     */
    protected void setup() {

    }

    public Map<EnumReferenceKey<?>, Object> getGenericReference() {
        if (genericReference == null) {
            genericReference = Maps.newHashMap();
            populateDefaultGenericReferences();
        }
        return genericReference;
    }

    public List<WorldStorage.Access<?>> getWorldStorages() {
        if (worldStorages == null) {
            worldStorages = Lists.newLinkedList();
        }
        return worldStorages;
    }

    public IKeyRegistry getKeyRegistry() {
        if (keyRegistry == null) {
            keyRegistry = new KeyRegistry();
        }
        return keyRegistry;
    }

    public String getModName() {
        return getContainer().getMetadata().getName();
    }

    public ModContainer getContainer() {
        if (container == null) {
            container = FabricLoader.getInstance().getModContainer(this.getModId()).orElseThrow();
        }
        return container;
    }

    public <V> void putGenericReference(EnumReferenceKey<V> key, V value) {
        getGenericReference().put(key, value);
    }

    private void populateDefaultGenericReferences() {
        genericReference.put(REFKEY_TEXTURE_PATH_GUI, "textures/gui/");
        genericReference.put(REFKEY_TEXTURE_PATH_MODELS, "textures/models/");
        genericReference.put(REFKEY_TEXTURE_PATH_SKINS, "textures/skins/");
        genericReference.put(REFKEY_RETROGEN, false);
        genericReference.put(REFKEY_CRASH_ON_INVALID_RECIPE, false);
        genericReference.put(REFKEY_CRASH_ON_MODCOMPAT_CRASH, false);
        genericReference.put(REFKEY_INFOBOOK_REWARDS, true);
    }

    @SuppressWarnings("unchecked")
    public <V> V getReferenceValue(EnumReferenceKey<V> key) {
        if (!getGenericReference().containsKey(key)) {
            throw new IllegalArgumentException("Could not find " + key + " as generic reference item.");
        }
        return (V) getGenericReference().get(key);
    }

    protected void onServerStarted(MinecraftServer server) {
        for (WorldStorage.Access<?> worldStorage : getWorldStorages()) {
            worldStorage.onStartedEvent(server);
        }
    }

    protected void onServerStopping(MinecraftServer server) {
        for (WorldStorage.Access<?> worldStorage : getWorldStorages()) {
            worldStorage.onStoppingEvent(server);
        }
    }

    public void registerWorldStorage(WorldStorage.Access<?> worldStorage) {
        getWorldStorages().add(worldStorage);
    }

    @Nullable
    public static ModBaseNeoForge<?> get(String modId) {
        return ModBaseCommon.getCommon(modId) instanceof ModBaseNeoForge<?> mod ? mod : null;
    }

    /**
     * Unique references to values that can be registered inside a mod.
     * @param <V> The type of value.
     */
    @Data
    public static class EnumReferenceKey<V> {

        private final String key;
        private final Class<V> type;

        private EnumReferenceKey(String key, Class<V> type) {
            this.key = key;
            this.type = type;
        }

        public static <V> EnumReferenceKey<V> create(String key, Class<V> type) {
            return new EnumReferenceKey<>(key, type);
        }

    }

}

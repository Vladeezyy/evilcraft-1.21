package org.cyclops.evilcraft.fabric.cyclopscore.init;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.config.ConfigHandlerCommon;
import org.cyclops.cyclopscore.helper.ModBaseCommon;
import org.cyclops.cyclopscore.init.ModBaseFabric;
import org.cyclops.cyclopscore.modcompat.ModCompatLoader;
import org.cyclops.cyclopscore.proxy.IClientProxyCommon;
import org.cyclops.cyclopscore.proxy.ICommonProxyCommon;
import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.KeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigHandler;
import org.cyclops.evilcraft.fabric.cyclopscore.network.PacketHandler;
import org.cyclops.evilcraft.fabric.cyclopscore.persist.world.WorldStorage;
import org.cyclops.evilcraft.fabric.cyclopscore.proxy.ICommonProxy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Base class of the mod on Fabric, with the API of Cyclops Core's NeoForge mod base that EvilCraft is written against.
 * @author rubensworks
 */
public abstract class ModBase<T extends ModBase<T>> extends ModBaseFabric<T> {

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
    private List<WorldStorage> worldStorages;
    private IKeyRegistry keyRegistry;
    private PacketHandler packetHandler;
    private ModContainer container;

    public ModBase(String modId, Consumer<T> instanceSetter) {
        super(modId, instanceSetter);
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
        super.onInitialize();

        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerAboutToStart);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);

        ICommonProxyCommon proxy = getProxy();
        if (proxy instanceof ICommonProxy commonProxy) {
            commonProxy.registerPacketHandlers(getPacketHandler());
            if (getModHelpers().getMinecraftHelpers().isClientSide()) {
                commonProxy.registerKeyBindings(getKeyRegistry());
            }
        }

        // Everything is registered now
        RegisterCapabilitiesEvent.fire();
        org.cyclops.evilcraft.fabric.event.ModLifecycle.fireAfterRegistries();
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

    public List<WorldStorage> getWorldStorages() {
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

    public PacketHandler getPacketHandler() {
        if (packetHandler == null) {
            packetHandler = new PacketHandler(getPacketHandlerCommon());
        }
        return packetHandler;
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

    @Override
    protected void loadModCompats(ModCompatLoader modCompatLoader) {
        super.loadModCompats(modCompatLoader);
    }

    @SuppressWarnings("unchecked")
    public <V> V getReferenceValue(EnumReferenceKey<V> key) {
        if (!getGenericReference().containsKey(key)) {
            throw new IllegalArgumentException("Could not find " + key + " as generic reference item.");
        }
        return (V) getGenericReference().get(key);
    }

    protected void onServerAboutToStart(MinecraftServer server) {
        for (WorldStorage worldStorage : getWorldStorages()) {
            worldStorage.onAboutToStartEvent(server);
        }
    }

    protected void onServerStarted(MinecraftServer server) {
        for (WorldStorage worldStorage : getWorldStorages()) {
            worldStorage.onStartedEvent(server);
        }
    }

    protected void onServerStopping(MinecraftServer server) {
        for (WorldStorage worldStorage : getWorldStorages()) {
            worldStorage.onStoppingEvent(server);
        }
    }

    public void registerWorldStorage(WorldStorage worldStorage) {
        getWorldStorages().add(worldStorage);
    }

    @Override
    protected abstract IClientProxyCommon constructClientProxy();

    @Override
    protected abstract ICommonProxyCommon constructCommonProxy();

    @Override
    protected void onConfigsRegister(ConfigHandlerCommon configHandler) {
        super.onConfigsRegister(configHandler);
        this.onConfigsRegister((ConfigHandler) configHandler);
    }

    protected void onConfigsRegister(ConfigHandler configHandler) {

    }

    @Override
    public void log(Level level, String message) {
        super.log(level, message);
    }



    @Nullable
    public static ModBase<?> get(String modId) {
        return ModBaseCommon.getCommon(modId) instanceof ModBase<?> mod ? mod : null;
    }

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

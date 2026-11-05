package org.cyclops.evilcraft.fabric.loot;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cyclops.cyclopscore.events.ILootTableModifyEvent;
import org.cyclops.evilcraft.Reference;

import javax.annotation.Nullable;
import java.io.Reader;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the loot modifiers in data/<namespace>/loot_modifiers (as NeoForge does) and
 * applies them to generated loot.
 */
public final class GlobalLootModifiers {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Identifier, MapCodec<? extends IGlobalLootModifier>> CODECS = Maps.newHashMap();
    public static final Codec<IGlobalLootModifier> CODEC = Identifier.CODEC.dispatch(
            modifier -> codecId(modifier.codec()),
            id -> {
                MapCodec<? extends IGlobalLootModifier> codec = CODECS.get(id);
                if (codec == null) {
                    throw new IllegalArgumentException("Unknown loot modifier type " + id);
                }
                return codec;
            });

    private static Map<Identifier, JsonElement> rawModifiers = Map.of();
    private static List<IGlobalLootModifier> modifiers = List.of();
    private static Map<LootTable, Identifier> lootTableIds = new IdentityHashMap<>();
    private static boolean registered = false;

    static {
        // Cyclops Core's item injection modifier, which EvilCraft's data uses
        CODECS.put(Identifier.fromNamespaceAndPath("cyclopscore", "inject_item"),
                org.cyclops.evilcraft.fabric.cyclopscore.loot.modifier.LootModifierInjectItem.CODEC.get());
    }

    private GlobalLootModifiers() {
    }

    public static void registerCodec(Identifier id, MapCodec<? extends IGlobalLootModifier> codec) {
        CODECS.put(id, codec);
        registerListeners();
    }

    private static Identifier codecId(MapCodec<? extends IGlobalLootModifier> codec) {
        for (Map.Entry<Identifier, MapCodec<? extends IGlobalLootModifier>> entry : CODECS.entrySet()) {
            if (entry.getValue() == codec) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Unregistered loot modifier codec " + codec);
    }

    private static synchronized void registerListeners() {
        if (registered) {
            return;
        }
        registered = true;
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.fromNamespaceAndPath(Reference.MOD_ID, "global_loot_modifiers");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                rawModifiers = readModifiers(resourceManager);
            }
        });
        // Parse with the registries of the server (conditions can refer to enchantments and other data)
        ServerLifecycleEvents.SERVER_STARTED.register(GlobalLootModifiers::parse);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> parse(server));
        ILootTableModifyEvent.EVENT.register(GlobalLootModifiers::apply);
    }

    /**
     * All files in the loot_modifiers folder of each namespace, as NeoForge 26.x does (the list file of 1.21.1 is gone).
     */
    private static Map<Identifier, JsonElement> readModifiers(ResourceManager resourceManager) {
        Map<Identifier, JsonElement> result = new LinkedHashMap<>();
        resourceManager.listResources("loot_modifiers", file -> file.getPath().endsWith(".json")).forEach((file, resource) -> {
            String path = file.getPath();
            Identifier id = file.withPath(path.substring("loot_modifiers/".length(), path.length() - ".json".length()));
            try (Reader reader = resource.openAsReader()) {
                result.put(id, JsonParser.parseReader(reader));
            } catch (Exception e) {
                LOGGER.error("Could not read loot modifier {}", id, e);
            }
        });
        return result;
    }

    private static void parse(MinecraftServer server) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        List<IGlobalLootModifier> parsed = Lists.newArrayList();
        rawModifiers.forEach((id, json) -> {
            // Only types that are known (other mods' NeoForge loot modifiers can be in the list)
            if (json.isJsonObject() && json.getAsJsonObject().has("type")
                    && CODECS.containsKey(Identifier.parse(json.getAsJsonObject().get("type").getAsString()))) {
                CODEC.parse(ops, json)
                        .resultOrPartial(error -> LOGGER.error("Could not parse loot modifier {}: {}", id, error))
                        .ifPresent(parsed::add);
            }
        });
        // Higher priorities first, as on NeoForge
        parsed.sort(java.util.Comparator.comparingInt(modifier -> -modifier.priority()));
        modifiers = parsed;
        Map<LootTable, Identifier> ids = new IdentityHashMap<>();
        server.reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE).listElements()
                .forEach(entry -> ids.put(entry.value(), entry.key().identifier()));
        lootTableIds = ids;
    }

    private static void apply(LootTable lootTable, LootContext context, ObjectArrayList<ItemStack> generatedLoot) {
        if (modifiers.isEmpty()) {
            return;
        }
        Identifier id = lootTableIds.get(lootTable);
        if (id == null) {
            return;
        }
        QUERIED.set(id);
        try {
            ObjectArrayList<ItemStack> loot = generatedLoot;
            for (IGlobalLootModifier modifier : modifiers) {
                loot = modifier.apply(loot, context);
            }
            if (loot != generatedLoot) {
                generatedLoot.clear();
                generatedLoot.addAll(loot);
            }
        } finally {
            QUERIED.remove();
        }
    }

    private static final ThreadLocal<Identifier> QUERIED = new ThreadLocal<>();

    /**
     * @return The id of the loot table whose loot is being modified, the queried loot table id of NeoForge's loot
     *         context.
     */
    @Nullable
    public static Identifier getQueriedLootTableId(LootContext context) {
        return QUERIED.get();
    }
}

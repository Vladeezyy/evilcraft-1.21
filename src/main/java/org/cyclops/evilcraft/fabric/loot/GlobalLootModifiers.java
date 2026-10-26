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
import net.minecraft.resources.ResourceLocation;
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
 * Loads the loot modifiers listed in data/neoforge/loot_modifiers/global_loot_modifiers.json (as NeoForge does) and
 * applies them to generated loot.
 */
public final class GlobalLootModifiers {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation LIST = ResourceLocation.fromNamespaceAndPath("neoforge", "loot_modifiers/global_loot_modifiers.json");
    private static final Map<ResourceLocation, MapCodec<? extends IGlobalLootModifier>> CODECS = Maps.newHashMap();
    public static final Codec<IGlobalLootModifier> CODEC = ResourceLocation.CODEC.dispatch(
            modifier -> codecId(modifier.codec()),
            id -> {
                MapCodec<? extends IGlobalLootModifier> codec = CODECS.get(id);
                if (codec == null) {
                    throw new IllegalArgumentException("Unknown loot modifier type " + id);
                }
                return codec;
            });

    private static Map<ResourceLocation, JsonElement> rawModifiers = Map.of();
    private static List<IGlobalLootModifier> modifiers = List.of();
    private static Map<LootTable, ResourceLocation> lootTableIds = new IdentityHashMap<>();
    private static boolean registered = false;

    static {
        // Cyclops Core's item injection modifier, which EvilCraft's data uses
        CODECS.put(ResourceLocation.fromNamespaceAndPath("cyclopscore", "inject_item"),
                org.cyclops.evilcraft.fabric.cyclopscore.loot.modifier.LootModifierInjectItem.CODEC.get());
    }

    private GlobalLootModifiers() {
    }

    public static void registerCodec(ResourceLocation id, MapCodec<? extends IGlobalLootModifier> codec) {
        CODECS.put(id, codec);
        registerListeners();
    }

    private static ResourceLocation codecId(MapCodec<? extends IGlobalLootModifier> codec) {
        for (Map.Entry<ResourceLocation, MapCodec<? extends IGlobalLootModifier>> entry : CODECS.entrySet()) {
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
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "global_loot_modifiers");
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

    private static Map<ResourceLocation, JsonElement> readModifiers(ResourceManager resourceManager) {
        List<ResourceLocation> entries = Lists.newArrayList();
        for (Resource resource : resourceManager.getResourceStack(LIST)) {
            try (Reader reader = resource.openAsReader()) {
                JsonObject list = JsonParser.parseReader(reader).getAsJsonObject();
                if (list.has("replace") && list.get("replace").getAsBoolean()) {
                    entries.clear();
                }
                for (JsonElement entry : list.getAsJsonArray("entries")) {
                    ResourceLocation id = ResourceLocation.parse(entry.getAsString());
                    if (!entries.contains(id)) {
                        entries.add(id);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Could not read loot modifier list {} of pack {}", LIST, resource.sourcePackId(), e);
            }
        }
        Map<ResourceLocation, JsonElement> result = new LinkedHashMap<>();
        for (ResourceLocation id : entries) {
            ResourceLocation file = id.withPath(path -> "loot_modifiers/" + path + ".json");
            resourceManager.getResource(file).ifPresentOrElse(resource -> {
                try (Reader reader = resource.openAsReader()) {
                    result.put(id, JsonParser.parseReader(reader));
                } catch (Exception e) {
                    LOGGER.error("Could not read loot modifier {}", id, e);
                }
            }, () -> LOGGER.warn("Missing loot modifier {}", id));
        }
        return result;
    }

    private static void parse(MinecraftServer server) {
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        List<IGlobalLootModifier> parsed = Lists.newArrayList();
        rawModifiers.forEach((id, json) -> {
            // Only types that are known (other mods' NeoForge loot modifiers can be in the list)
            if (json.isJsonObject() && json.getAsJsonObject().has("type")
                    && CODECS.containsKey(ResourceLocation.parse(json.getAsJsonObject().get("type").getAsString()))) {
                CODEC.parse(ops, json)
                        .resultOrPartial(error -> LOGGER.error("Could not parse loot modifier {}: {}", id, error))
                        .ifPresent(parsed::add);
            }
        });
        modifiers = parsed;
        Map<LootTable, ResourceLocation> ids = new IdentityHashMap<>();
        server.reloadableRegistries().get().registryOrThrow(Registries.LOOT_TABLE).entrySet()
                .forEach(entry -> ids.put(entry.getValue(), entry.getKey().location()));
        lootTableIds = ids;
    }

    private static void apply(LootTable lootTable, LootContext context, ObjectArrayList<ItemStack> generatedLoot) {
        if (modifiers.isEmpty()) {
            return;
        }
        ResourceLocation id = lootTableIds.get(lootTable);
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

    private static final ThreadLocal<ResourceLocation> QUERIED = new ThreadLocal<>();

    /**
     * @return The id of the loot table whose loot is being modified, the queried loot table id of NeoForge's loot
     *         context.
     */
    @Nullable
    public static ResourceLocation getQueriedLootTableId(LootContext context) {
        return QUERIED.get();
    }
}

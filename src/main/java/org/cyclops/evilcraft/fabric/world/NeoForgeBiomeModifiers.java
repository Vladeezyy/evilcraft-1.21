package org.cyclops.evilcraft.fabric.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Applies the mod's NeoForge biome modifiers (data/[mod]/neoforge/biome_modifier/*.json in the mod jar,
 * types add_features and add_spawns) with Fabric's biome modification API, so the data is kept in one place.
 */
public final class NeoForgeBiomeModifiers {

    private static final Logger LOGGER = LogManager.getLogger();

    private NeoForgeBiomeModifiers() {
    }

    public static void register(String modId) {
        ModContainer container = FabricLoader.getInstance().getModContainer(modId).orElseThrow();
        container.findPath("data/" + modId + "/neoforge/biome_modifier").ifPresent(dir -> {
            try (Stream<Path> files = Files.list(dir)) {
                files.filter(p -> p.toString().endsWith(".json")).sorted().forEach(file -> {
                    String name = file.getFileName().toString();
                    Identifier id = Identifier.fromNamespaceAndPath(modId, name.substring(0, name.length() - ".json".length()));
                    try (Reader reader = Files.newBufferedReader(file)) {
                        apply(id, JsonParser.parseReader(reader).getAsJsonObject());
                    } catch (IOException | RuntimeException e) {
                        LOGGER.error("Could not apply biome modifier {}", id, e);
                    }
                });
            } catch (IOException e) {
                LOGGER.error("Could not read the biome modifiers of {}", modId, e);
            }
        });
    }

    private static void apply(Identifier id, JsonObject json) {
        String type = json.get("type").getAsString();
        Predicate<BiomeSelectionContext> biomes = biomes(json.get("biomes"));
        switch (type) {
            case "neoforge:add_features" -> {
                GenerationStep.Decoration step = GenerationStep.Decoration.valueOf(json.get("step").getAsString().toUpperCase(Locale.ROOT));
                List<ResourceKey<PlacedFeature>> features = new ArrayList<>();
                for (String feature : strings(json.get("features"))) {
                    features.add(ResourceKey.create(Registries.PLACED_FEATURE, Identifier.parse(feature)));
                }
                BiomeModifications.create(id).add(ModificationPhase.ADDITIONS, biomes, context -> {
                    for (ResourceKey<PlacedFeature> feature : features) {
                        context.getGenerationSettings().addFeature(step, feature);
                    }
                });
            }
            case "neoforge:add_spawns" -> {
                JsonElement spawnersJson = json.get("spawners");
                JsonArray spawners = spawnersJson.isJsonArray() ? spawnersJson.getAsJsonArray() : new JsonArray();
                if (!spawnersJson.isJsonArray()) {
                    spawners.add(spawnersJson);
                }
                BiomeModifications.create(id).add(ModificationPhase.ADDITIONS, biomes, context -> {
                    for (JsonElement element : spawners) {
                        JsonObject spawner = element.getAsJsonObject();
                        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(spawner.get("type").getAsString()));
                        context.getSpawnSettings().addSpawn(entityType.getCategory(), new MobSpawnSettings.SpawnerData(entityType,
                                spawner.get("minCount").getAsInt(), spawner.get("maxCount").getAsInt()), spawner.get("weight").getAsInt());
                    }
                });
            }
            default -> LOGGER.warn("Unsupported biome modifier type {} in {}", type, id);
        }
    }

    private static Predicate<BiomeSelectionContext> biomes(JsonElement json) {
        List<Predicate<BiomeSelectionContext>> selectors = new ArrayList<>();
        for (String biome : strings(json)) {
            if (biome.startsWith("#")) {
                selectors.add(BiomeSelectors.tag(TagKey.create(Registries.BIOME, Identifier.parse(biome.substring(1)))));
            } else {
                selectors.add(BiomeSelectors.includeByKey(ResourceKey.create(Registries.BIOME, Identifier.parse(biome))));
            }
        }
        return context -> selectors.stream().anyMatch(selector -> selector.test(context));
    }

    private static List<String> strings(JsonElement json) {
        List<String> list = new ArrayList<>();
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(e -> list.add(e.getAsString()));
        } else {
            list.add(json.getAsString());
        }
        return list;
    }
}

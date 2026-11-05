package org.cyclops.evilcraft.fabric.client;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.io.Reader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Fabric does not read the "render_type" of block models: take it from the models of the blocks of a namespace
 * (blockstate, model, parents) when resources are loaded, and put the blocks in that render layer.
 */
@Environment(EnvType.CLIENT)
public final class ModelRenderTypes {

    private static final Map<String, RenderType> RENDER_TYPES = Map.of(
            "minecraft:solid", RenderType.solid(),
            "minecraft:cutout", RenderType.cutout(),
            "minecraft:cutout_mipped", RenderType.cutoutMipped(),
            "minecraft:translucent", RenderType.translucent());

    private ModelRenderTypes() {
    }

    public static void init(String namespace) {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.fromNamespaceAndPath(namespace, "model_render_types");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                for (Block block : BuiltInRegistries.BLOCK) {
                    Identifier id = BuiltInRegistries.BLOCK.getKey(block);
                    if (id.getNamespace().equals(namespace)) {
                        RenderType renderType = getRenderType(resourceManager, id);
                        if (renderType != null) {
                            BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
                        }
                    }
                }
            }
        });
    }

    @Nullable
    private static RenderType getRenderType(ResourceManager resourceManager, Identifier blockId) {
        JsonObject blockState = read(resourceManager, blockId.withPath(path -> "blockstates/" + path + ".json"));
        if (blockState == null) {
            return null;
        }
        Set<String> models = Sets.newLinkedHashSet();
        if (blockState.has("variants")) {
            for (Map.Entry<String, JsonElement> variant : blockState.getAsJsonObject("variants").entrySet()) {
                collectModels(variant.getValue(), models);
            }
        }
        if (blockState.has("multipart")) {
            for (JsonElement part : blockState.getAsJsonArray("multipart")) {
                collectModels(part.getAsJsonObject().get("apply"), models);
            }
        }
        for (String model : models) {
            String renderType = findRenderType(resourceManager, Identifier.parse(model), 0);
            if (renderType != null) {
                return RENDER_TYPES.get(renderType.contains(":") ? renderType : "minecraft:" + renderType);
            }
        }
        return null;
    }

    private static void collectModels(JsonElement element, Set<String> models) {
        if (element == null) {
            return;
        }
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(e -> collectModels(e, models));
        } else if (element.isJsonObject() && element.getAsJsonObject().has("model")) {
            models.add(element.getAsJsonObject().get("model").getAsString());
        }
    }

    @Nullable
    private static String findRenderType(ResourceManager resourceManager, Identifier model, int depth) {
        JsonObject json = read(resourceManager, model.withPath(path -> "models/" + path + ".json"));
        if (json == null || depth > 16) {
            return null;
        }
        if (json.has("render_type")) {
            return json.get("render_type").getAsString();
        }
        if (json.has("parent")) {
            return findRenderType(resourceManager, Identifier.parse(json.get("parent").getAsString()), depth + 1);
        }
        return null;
    }

    @Nullable
    private static JsonObject read(ResourceManager resourceManager, Identifier file) {
        Optional<Resource> resource = resourceManager.getResource(file);
        if (resource.isEmpty()) {
            return null;
        }
        try (Reader reader = resource.get().openAsReader()) {
            JsonElement json = JsonParser.parseReader(reader);
            return json.isJsonObject() ? json.getAsJsonObject() : null;
        } catch (Exception e) {
            return null;
        }
    }
}

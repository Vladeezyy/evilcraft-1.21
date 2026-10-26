package org.cyclops.evilcraft.fabric.client.model;

import com.google.gson.JsonParseException;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Custom model loaders: model jsons of a namespace with a "loader" key are read by the loader of that id, extra
 * models are baked as standalone models.
 */
@Environment(EnvType.CLIENT)
public final class ModelLoaders {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, IGeometryLoader<?>> LOADERS = Maps.newHashMap();
    private static final Set<Supplier<? extends Iterable<ResourceLocation>>> ADDITIONAL_MODELS = Sets.newLinkedHashSet();
    private static final JsonDeserializationContext CONTEXT = new JsonDeserializationContext() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T deserialize(JsonElement json, Type typeOfT) {
            if (typeOfT == BlockModel.class) {
                return (T) BlockModel.fromString(json.toString());
            }
            throw new JsonParseException("Can not deserialize " + typeOfT + " in a custom model");
        }
    };

    private ModelLoaders() {
    }

    public static void registerLoader(ResourceLocation id, IGeometryLoader<?> loader) {
        LOADERS.put(id, loader);
    }

    /**
     * @param models Models to bake in addition to the models of blocks and items, read when models are loaded.
     */
    public static void registerAdditionalModels(Supplier<? extends Iterable<ResourceLocation>> models) {
        ADDITIONAL_MODELS.add(models);
    }

    /**
     * Register the model loading plugin for the given namespace.
     */
    public static void init(String namespace) {
        PreparableModelLoadingPlugin.register(
                (resourceManager, executor) -> load(resourceManager, executor, namespace),
                (models, pluginContext) -> {
                    for (Supplier<? extends Iterable<ResourceLocation>> additional : ADDITIONAL_MODELS) {
                        for (ResourceLocation id : additional.get()) {
                            pluginContext.addModels(id);
                        }
                    }
                    pluginContext.modifyModelOnLoad().register((original, context) -> {
                        ResourceLocation id = context.resourceId();
                        if (id != null) {
                            UnbakedModel custom = models.get(id);
                            if (custom != null) {
                                return custom;
                            }
                        }
                        return original;
                    });
                });
    }

    private static CompletableFuture<Map<ResourceLocation, UnbakedModel>> load(ResourceManager resourceManager, Executor executor, String namespace) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, UnbakedModel> models = Maps.newHashMap();
            Map<ResourceLocation, Resource> resources = resourceManager.listResources("models",
                    id -> id.getNamespace().equals(namespace) && id.getPath().endsWith(".json"));
            resources.forEach((file, resource) -> {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement json = JsonParser.parseReader(reader);
                    if (json.isJsonObject() && json.getAsJsonObject().has("loader")) {
                        JsonObject object = json.getAsJsonObject();
                        IGeometryLoader<?> loader = LOADERS.get(ResourceLocation.parse(object.get("loader").getAsString()));
                        if (loader != null) {
                            String path = file.getPath();
                            ResourceLocation modelId = file.withPath(path.substring("models/".length(), path.length() - ".json".length()));
                            models.put(modelId, loader.read(object, CONTEXT));
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Could not load the custom model {}", file, e);
                }
            });
            return models;
        }, executor);
    }
}

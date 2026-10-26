package org.cyclops.evilcraft.fabric.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.model.UnbakedModel;

/**
 * Reads a model json with a "loader" of this mod into an unbaked model.
 */
@FunctionalInterface
public interface IGeometryLoader<T extends UnbakedModel> {
    T read(JsonObject modelContents, JsonDeserializationContext deserializationContext);
}

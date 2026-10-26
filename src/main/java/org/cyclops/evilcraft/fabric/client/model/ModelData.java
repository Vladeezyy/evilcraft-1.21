package org.cyclops.evilcraft.fabric.client.model;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Immutable data of a block (taken from its block entity) that a dynamic model renders.
 */
public final class ModelData {

    public static final ModelData EMPTY = new ModelData(Map.of());

    private final Map<ModelProperty<?>, Object> properties;

    private ModelData(Map<ModelProperty<?>, Object> properties) {
        this.properties = properties;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(ModelProperty<T> property) {
        return (T) properties.get(property);
    }

    public boolean has(ModelProperty<?> property) {
        return properties.containsKey(property);
    }

    public static final class Builder {
        private final Map<ModelProperty<?>, Object> properties = new IdentityHashMap<>();

        public <T> Builder with(ModelProperty<T> property, T value) {
            properties.put(property, value);
            return this;
        }

        public ModelData build() {
            return properties.isEmpty() ? EMPTY : new ModelData(properties);
        }
    }
}

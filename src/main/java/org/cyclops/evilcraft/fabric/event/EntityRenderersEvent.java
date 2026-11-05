package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The part of NeoForge's event of this name that the mod uses.
 */
@Environment(EnvType.CLIENT)
public final class EntityRenderersEvent {

    private EntityRenderersEvent() {
    }

    public static final class RegisterLayerDefinitions {
        public static void addListener(Consumer<RegisterLayerDefinitions> listener) {
            listener.accept(new RegisterLayerDefinitions());
        }

        public void registerLayerDefinition(ModelLayerLocation layer, Supplier<LayerDefinition> definition) {
            ModelLayerRegistry.registerModelLayer(layer, definition::get);
        }
    }
}

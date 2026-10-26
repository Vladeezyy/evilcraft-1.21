package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Consumer;

/**
 * Registers the default attributes of entity types once all registries are filled.
 */
public class EntityAttributeCreationEvent {

    public static void addListener(Consumer<EntityAttributeCreationEvent> listener) {
        ModLifecycle.afterRegistries(() -> listener.accept(new EntityAttributeCreationEvent()));
    }

    public void put(EntityType<? extends LivingEntity> entityType, AttributeSupplier attributes) {
        FabricDefaultAttributeRegistry.register(entityType, attributes);
    }
}

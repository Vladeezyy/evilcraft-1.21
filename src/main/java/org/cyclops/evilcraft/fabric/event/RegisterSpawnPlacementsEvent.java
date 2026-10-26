package org.cyclops.evilcraft.fabric.event;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Consumer;

/**
 * Registers spawn placements of entity types once all registries are filled.
 */
public class RegisterSpawnPlacementsEvent {

    public enum Operation {
        AND, OR, REPLACE
    }

    public static void addListener(Consumer<RegisterSpawnPlacementsEvent> listener) {
        ModLifecycle.afterRegistries(() -> listener.accept(new RegisterSpawnPlacementsEvent()));
    }

    public <T extends Mob> void register(EntityType<T> entityType, SpawnPlacementType placementType, Heightmap.Types heightmap,
                                         SpawnPlacements.SpawnPredicate<T> predicate, Operation operation) {
        // Only used for this mod's own entity types, which have no placement yet
        SpawnPlacements.register(entityType, placementType, heightmap, predicate);
    }
}

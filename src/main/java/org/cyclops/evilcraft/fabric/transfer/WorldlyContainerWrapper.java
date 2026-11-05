package org.cyclops.evilcraft.fabric.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;

/**
 * The slots of a side of a container as a handler.
 */
public final class WorldlyContainerWrapper {

    private WorldlyContainerWrapper() {
    }

    public static ResourceHandler<ItemResource> of(WorldlyContainer container, Direction side) {
        return StorageResourceHandler.ofItem(ContainerStorage.of(container, side));
    }
}

package org.cyclops.evilcraft.fabric.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.world.Container;

/**
 * A vanilla container as a handler, through Fabric's cached wrappers.
 */
public final class VanillaContainerWrapper {

    private VanillaContainerWrapper() {
    }

    public static ResourceHandler<ItemResource> of(Container container) {
        return StorageResourceHandler.ofItem(InventoryStorage.of(container, null));
    }
}

package org.cyclops.evilcraft.fabric.capabilities;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.cyclops.evilcraft.Reference;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;
import org.cyclops.evilcraft.fabric.transfer.ItemAccess;
import org.cyclops.evilcraft.fabric.transfer.ItemResource;
import org.cyclops.evilcraft.fabric.transfer.ResourceHandler;
import org.cyclops.evilcraft.fabric.transfer.StorageResourceHandler;

/**
 * The capabilities EvilCraft uses, with the names of NeoForge. Everything that is not of this mod (vanilla, buckets,
 * tanks and chests of other mods) is found through Fabric's transfer API.
 */
public final class Capabilities {

    private Capabilities() {
    }

    public static final class Fluid {
        public static final BlockCapability<ResourceHandler<FluidResource>, Direction> BLOCK = BlockCapability.createSided(
                id("fluid_handler"), ResourceHandler.<FluidResource>asClass());
        public static final ItemCapability<ResourceHandler<FluidResource>, ItemAccess> ITEM = ItemCapability.create(
                id("fluid_handler"), ResourceHandler.<FluidResource>asClass(), ItemAccess.class);

        static {
            BLOCK.lookup().registerFallback((level, pos, state, blockEntity, side) -> {
                Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, state, blockEntity, side);
                return storage == null ? null : StorageResourceHandler.ofFluid(storage);
            });
            ITEM.lookup().registerFallback((stack, access) -> {
                Storage<FluidVariant> storage = access.toContext().find(FluidStorage.ITEM);
                return storage == null ? null : StorageResourceHandler.ofFluid(storage);
            });
        }

        private Fluid() {
        }
    }

    public static final class Item {
        public static final BlockCapability<ResourceHandler<ItemResource>, Direction> BLOCK = BlockCapability.createSided(
                id("item_handler"), ResourceHandler.<ItemResource>asClass());
        public static final ItemCapability<ResourceHandler<ItemResource>, ItemAccess> ITEM = ItemCapability.create(
                id("item_handler"), ResourceHandler.<ItemResource>asClass(), ItemAccess.class);

        static {
            BLOCK.lookup().registerFallback((level, pos, state, blockEntity, side) -> {
                Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, state, blockEntity, side);
                return storage == null ? null : StorageResourceHandler.ofItem(storage);
            });
        }

        private Item() {
        }
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Reference.MOD_ID, path);
    }

    /**
     * Load the lookups (and their fallbacks) early.
     */
    public static void load() {
        Fluid.BLOCK.lookup();
        Item.BLOCK.lookup();
    }
}

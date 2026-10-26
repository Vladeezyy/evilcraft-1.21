package org.cyclops.evilcraft.fabric.capabilities;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import org.cyclops.evilcraft.Reference;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandlerItem;
import org.cyclops.evilcraft.fabric.fluids.capability.wrappers.FluidBucketWrapper;
import org.cyclops.evilcraft.fabric.fluids.transfer.FluidHandlerItemStorage;
import org.cyclops.evilcraft.fabric.fluids.transfer.FluidHandlerStorage;
import org.cyclops.evilcraft.fabric.fluids.transfer.StorageFluidHandler;
import org.cyclops.evilcraft.fabric.fluids.transfer.StorageFluidHandlerItem;

/**
 * The capabilities EvilCraft uses. Fluid handlers fall back to Fabric's transfer API for blocks and items of other
 * mods; item handlers are Fabric's item storages.
 */
public final class Capabilities {

    private Capabilities() {
    }

    public static final class FluidHandler {
        public static final BlockCapability<IFluidHandler, Direction> BLOCK = BlockCapability.createSided(
                ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fluid_handler"), IFluidHandler.class);
        public static final ItemCapability<IFluidHandlerItem, Void> ITEM = ItemCapability.createVoid(
                ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "fluid_handler_item"), IFluidHandlerItem.class);

        static {
            BLOCK.lookup().registerFallback((level, pos, state, blockEntity, side) -> {
                Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, state, blockEntity, side);
                if (storage == null) {
                    return null;
                }
                if (storage instanceof FluidHandlerStorage wrapped) {
                    return wrapped.getHandler();
                }
                return new StorageFluidHandler(storage);
            });
            ITEM.lookup().registerFallback((stack, context) -> {
                if (stack.getItem() instanceof BucketItem || stack.is(Items.BUCKET)) {
                    return new FluidBucketWrapper(stack);
                }
                return StorageFluidHandlerItem.of(stack);
            });
        }

        private FluidHandler() {
        }
    }

    public static final class ItemHandler {
        public static final BlockCapability<Storage<ItemVariant>, Direction> BLOCK = BlockCapability.of(ItemStorage.SIDED);

        private ItemHandler() {
        }
    }

    /**
     * Load the lookups (and their fallbacks) early.
     */
    public static void load() {
        FluidHandler.BLOCK.lookup();
        ItemHandler.BLOCK.lookup();
        FluidHandlerItemStorage.class.getName();
    }
}

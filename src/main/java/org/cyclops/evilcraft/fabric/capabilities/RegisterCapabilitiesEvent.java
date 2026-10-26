package org.cyclops.evilcraft.fabric.capabilities;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandlerItem;
import org.cyclops.evilcraft.fabric.fluids.transfer.FluidHandlerItemStorage;
import org.cyclops.evilcraft.fabric.fluids.transfer.FluidHandlerStorage;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Collects the capability registrations of the mod, in the style of NeoForge's event of the same name.
 * Fluid handlers are also exposed to Fabric's transfer API.
 */
public class RegisterCapabilitiesEvent {

    private static final List<Consumer<RegisterCapabilitiesEvent>> LISTENERS = Lists.newArrayList();
    private static boolean fired = false;

    public static void addListener(Consumer<RegisterCapabilitiesEvent> listener) {
        if (fired) {
            listener.accept(new RegisterCapabilitiesEvent());
        } else {
            LISTENERS.add(listener);
        }
    }

    /**
     * Called once all registries are filled.
     */
    public static void fire() {
        fired = true;
        RegisterCapabilitiesEvent event = new RegisterCapabilitiesEvent();
        for (Consumer<RegisterCapabilitiesEvent> listener : LISTENERS) {
            listener.accept(event);
        }
        LISTENERS.clear();
    }

    @SuppressWarnings("unchecked")
    public <T, C, BE extends BlockEntity> void registerBlockEntity(BlockCapability<T, C> capability, BlockEntityType<BE> blockEntityType,
                                                                    ICapabilityProvider<? super BE, C, T> provider) {
        capability.lookup().registerForBlockEntity((be, context) -> provider.getCapability(be, context), blockEntityType);
        if (capability == (Object) Capabilities.FluidHandler.BLOCK) {
            ICapabilityProvider<? super BE, Direction, IFluidHandler> fluidProvider = (ICapabilityProvider<? super BE, Direction, IFluidHandler>) provider;
            FluidStorage.SIDED.registerForBlockEntity((be, side) -> {
                IFluidHandler handler = fluidProvider.getCapability(be, side);
                return handler == null ? null : new FluidHandlerStorage(handler);
            }, blockEntityType);
        }
    }

    @SuppressWarnings("unchecked")
    public <T, C> void registerBlock(BlockCapability<T, C> capability, IBlockCapabilityProvider<T, C> provider, Block... blocks) {
        capability.lookup().registerForBlocks(provider::getCapability, blocks);
        if (capability == (Object) Capabilities.FluidHandler.BLOCK) {
            IBlockCapabilityProvider<IFluidHandler, Direction> fluidProvider = (IBlockCapabilityProvider<IFluidHandler, Direction>) provider;
            FluidStorage.SIDED.registerForBlocks((level, pos, state, be, side) -> {
                IFluidHandler handler = fluidProvider.getCapability(level, pos, state, be, side);
                return handler == null ? null : new FluidHandlerStorage(handler);
            }, blocks);
        }
    }

    @SuppressWarnings("unchecked")
    public <T, C> void registerItem(ItemCapability<T, C> capability, ICapabilityProvider<net.minecraft.world.item.ItemStack, C, T> provider, ItemLike... items) {
        capability.lookup().registerForItems(provider::getCapability, items);
        if (capability == (Object) Capabilities.FluidHandler.ITEM) {
            ICapabilityProvider<net.minecraft.world.item.ItemStack, Void, IFluidHandlerItem> fluidProvider =
                    (ICapabilityProvider<net.minecraft.world.item.ItemStack, Void, IFluidHandlerItem>) provider;
            FluidStorage.ITEM.registerForItems((stack, context) -> new FluidHandlerItemStorage(context, fluidProvider), items);
        }
    }

    public boolean isItemRegistered(ItemCapability<?, ?> capability, Item item) {
        return Arrays.stream(new Item[]{item}).anyMatch(i -> capability.lookup().getProvider(i) != null);
    }
}

package org.cyclops.evilcraft.fabric.capabilities;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.cyclops.evilcraft.fabric.transfer.FluidResource;
import org.cyclops.evilcraft.fabric.transfer.ItemAccess;
import org.cyclops.evilcraft.fabric.transfer.ItemResource;
import org.cyclops.evilcraft.fabric.transfer.ResourceHandler;
import org.cyclops.evilcraft.fabric.transfer.ResourceHandlerStorage;

import java.util.List;
import java.util.function.Consumer;

/**
 * Collects the capability registrations of the mod, in the style of NeoForge's event of the same name.
 * Fluid and item handlers are also exposed to Fabric's transfer API, for hoppers and the pipes of other mods.
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
        if (capability == (Object) Capabilities.Fluid.BLOCK) {
            var fluid = (ICapabilityProvider<? super BE, Direction, ResourceHandler<FluidResource>>) provider;
            FluidStorage.SIDED.registerForBlockEntity((be, side) -> {
                ResourceHandler<FluidResource> handler = fluid.getCapability(be, side);
                return handler == null ? null : ResourceHandlerStorage.ofFluid(handler);
            }, blockEntityType);
        } else if (capability == (Object) Capabilities.Item.BLOCK) {
            var item = (ICapabilityProvider<? super BE, Direction, ResourceHandler<ItemResource>>) provider;
            ItemStorage.SIDED.registerForBlockEntity((be, side) -> {
                ResourceHandler<ItemResource> handler = item.getCapability(be, side);
                return handler == null ? null : ResourceHandlerStorage.ofItem(handler);
            }, blockEntityType);
        }
    }

    @SuppressWarnings("unchecked")
    public <T, C> void registerBlock(BlockCapability<T, C> capability, IBlockCapabilityProvider<T, C> provider, Block... blocks) {
        capability.lookup().registerForBlocks(provider::getCapability, blocks);
        if (capability == (Object) Capabilities.Fluid.BLOCK) {
            var fluid = (IBlockCapabilityProvider<ResourceHandler<FluidResource>, Direction>) provider;
            FluidStorage.SIDED.registerForBlocks((level, pos, state, be, side) -> {
                ResourceHandler<FluidResource> handler = fluid.getCapability(level, pos, state, be, side);
                return handler == null ? null : ResourceHandlerStorage.ofFluid(handler);
            }, blocks);
        }
    }

    @SuppressWarnings("unchecked")
    public <T, C> void registerItem(ItemCapability<T, C> capability, ICapabilityProvider<ItemStack, C, T> provider, ItemLike... items) {
        capability.lookup().registerForItems(provider::getCapability, items);
        if (capability == (Object) Capabilities.Fluid.ITEM) {
            var fluid = (ICapabilityProvider<ItemStack, ItemAccess, ResourceHandler<FluidResource>>) provider;
            FluidStorage.ITEM.registerForItems((stack, context) -> {
                ResourceHandler<FluidResource> handler = fluid.getCapability(stack, ItemAccess.of(context));
                return handler == null ? null : ResourceHandlerStorage.ofFluid(handler);
            }, items);
        }
    }

    public boolean isItemRegistered(ItemCapability<?, ?> capability, Item item) {
        return capability.lookup().getProvider(item) != null;
    }
}

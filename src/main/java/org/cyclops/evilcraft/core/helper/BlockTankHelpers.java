package org.cyclops.evilcraft.core.helper;

import net.fabricmc.fabric.api.util.TriState;
import org.cyclops.evilcraft.fabric.event.PlayerInteractEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.TriState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.cyclops.evilcraft.fabric.cyclopscore.Capabilities;
import org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid.IFluidHandlerCapacity;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.evilcraft.fabric.cyclopscore.item.DamageIndicatedItemComponent;
import org.cyclops.evilcraft.fabric.cyclopscore.item.IInformationProvider;
import org.cyclops.evilcraft.core.block.IBlockTank;

import java.util.Optional;

/**
 * Helpers related to blocks with tanks.
 * @author rubensworks
 */
public class BlockTankHelpers {

    static {
        PlayerInteractEvent.RightClickBlock.addListener(new BlockTankHelpers()::onRightClick);
    }

    private BlockTankHelpers() {

    }

    /**
     * Get info for a given itemStack.
     * @param itemStack The itemStack that must be given information.
     * @return Information for that itemStack.
     */
    public static MutableComponent getInfoTank(ItemStack itemStack) {
        FluidStack fluidStack = FluidUtil.getStack(org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.ITEM.getCapability(itemStack, ItemAccess.forStack(itemStack)), 0);
        int amount = fluidStack.getAmount();
        int capacity = Optional.ofNullable(org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.ITEM.getCapability(itemStack, ItemAccess.forStack(itemStack)))
                .map(handler -> handler.getCapacityAsInt(0, FluidResource.EMPTY))
                .orElse(0);
        return DamageIndicatedItemComponent.getInfo(fluidStack, amount, capacity)
                .withStyle(IInformationProvider.ITEM_PREFIX);
    }

    /**
     * Convert fluid capabilities of tile to item.
     * @param tile The tile that has already been removed from the world.
     * @param itemStack The input itemstack.
     * @return The resulting itemstack.
     */
    public static ItemStack tileDataToItemStack(BlockEntity tile, ItemStack itemStack) {
        ResourceHandler<FluidResource> fluidHandlerTile = org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), null, tile, null);
        if (fluidHandlerTile != null) {
            ItemAccess itemStackItemAccess = ItemAccess.forStack(itemStack);
            IFluidHandlerCapacity fluidHandlerItemCapacity = Capabilities.Item.FLUID_HANDLER_CAPACITY.getCapability(itemStack, itemStackItemAccess);
            if (fluidHandlerItemCapacity != null) {
                if (fluidHandlerTile instanceof IFluidHandlerCapacity fluidHandlerCapacity) {
                    try (var tx = Transaction.openRoot()) {
                        fluidHandlerCapacity.setTankCapacity(0, fluidHandlerItemCapacity.getTankCapacity(0), tx);
                        tx.commit();
                    }
                }
            }
            ResourceHandler<FluidResource> fluidHandlerItem = org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.ITEM.getCapability(itemStack, itemStackItemAccess);
            if (fluidHandlerItem != null) {
                IModHelpers.get().getFluidHelpers().move(fluidHandlerTile, fluidHandlerItem, Integer.MAX_VALUE, null, false, false);
            }
            return itemStackItemAccess.getResource().toStack(itemStackItemAccess.getAmount());
        }
        return itemStack;
    }

    /**
     * Convert fluid capabilities of item to tile.
     * @param itemStack The itemstack.
     * @param tile The tile that has already been removed from the world.
     */
    public static void itemStackDataToTile(ItemStack itemStack, BlockEntity tile) {
        ResourceHandler<FluidResource> fluidHandlerTile = org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), null, tile, null);
        if (fluidHandlerTile != null) {
            IFluidHandlerCapacity fluidHandlerItemCapacity = Capabilities.Item.FLUID_HANDLER_CAPACITY.getCapability(itemStack, ItemAccess.forStack(itemStack));
            if (fluidHandlerItemCapacity != null) {
                if (fluidHandlerTile instanceof IFluidHandlerCapacity fluidHandlerCapacity) {
                    try (var tx = Transaction.openRoot()) {
                        fluidHandlerCapacity.setTankCapacity(0, fluidHandlerItemCapacity.getTankCapacity(0), tx);
                        tx.commit();
                    }
                }
            }

            ResourceHandler<FluidResource> fluidHandlerItem = org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.ITEM.getCapability(itemStack, ItemAccess.forStack(itemStack));
            if (fluidHandlerItem != null) {
                IModHelpers.get().getFluidHelpers().move(fluidHandlerItem, fluidHandlerTile, Integer.MAX_VALUE, null, false, false);
            }
        }
    }

    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        // Force allow shift-right clicking with a fluid container passing through to this block
        if (!event.getItemStack().isEmpty()
                && org.cyclops.evilcraft.fabric.capabilities.Capabilities.Fluid.ITEM.getCapability(event.getItemStack(), ItemAccess.forStack(event.getItemStack())) != null
                && event.getLevel().getBlockState(event.getPos()).getBlock() instanceof IBlockTank) {
            event.setUseBlock(TriState.TRUE);
        }
    }


}

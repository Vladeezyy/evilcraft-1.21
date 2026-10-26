package org.cyclops.evilcraft.core.helper;

import net.fabricmc.fabric.api.util.TriState;
import org.cyclops.evilcraft.fabric.event.PlayerInteractEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.fluids.FluidActionResult;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.FluidUtil;
import org.cyclops.evilcraft.fabric.fluids.IFluidTank;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandlerItem;
import org.cyclops.evilcraft.fabric.fluids.capability.templates.FluidTank;
import org.cyclops.evilcraft.fabric.cyclopscore.Capabilities;
import org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid.IFluidHandlerItemCapacity;
import org.cyclops.evilcraft.fabric.cyclopscore.item.DamageIndicatedItemComponent;
import org.cyclops.evilcraft.fabric.cyclopscore.item.IInformationProvider;
import org.cyclops.evilcraft.core.block.IBlockTank;
import org.cyclops.evilcraft.core.fluid.FluidContainerItemWrapperWithSimulation;

import javax.annotation.Nonnull;

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
    @Environment(EnvType.CLIENT)
    public static MutableComponent getInfoTank(ItemStack itemStack) {
        FluidStack fluidStack = FluidUtil.getFluidContained(itemStack).orElse(FluidStack.EMPTY);
        int amount = fluidStack.getAmount();
        int capacity = FluidUtil.getFluidHandler(itemStack)
                .map(handler -> ((IFluidHandlerItemCapacity) handler).getCapacity())
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
        IFluidHandler fluidHandlerTile = org.cyclops.evilcraft.fabric.capabilities.Capabilities.FluidHandler.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), null, tile, null);
        if (fluidHandlerTile != null) {
            IFluidHandlerItemCapacity fluidHandlerItemCapacity = Capabilities.Item.FLUID_HANDLER_CAPACITY.getCapability(itemStack, null);
            if (fluidHandlerItemCapacity != null) {
                if (fluidHandlerTile instanceof IFluidTank) {
                    IFluidTank fluidTank = (IFluidTank) fluidHandlerTile;
                    fluidHandlerItemCapacity.setCapacity(fluidTank.getCapacity());
                    itemStack = fluidHandlerItemCapacity.getContainer();
                }
            }
            IFluidHandlerItem fluidHandlerItem = org.cyclops.evilcraft.fabric.capabilities.Capabilities.FluidHandler.ITEM.getCapability(itemStack, null);
            if (fluidHandlerItem != null) {
                FluidActionResult res = FluidUtil.tryFillContainer(itemStack, fluidHandlerTile, Integer.MAX_VALUE, null, true);
                if (res.isSuccess()) {
                    itemStack = res.getResult();
                }
            }
        }
        return itemStack;
    }

    /**
     * Convert fluid capabilities of item to tile.
     * @param itemStack The itemstack.
     * @param tile The tile that has already been removed from the world.
     */
    public static void itemStackDataToTile(ItemStack itemStack, BlockEntity tile) {
        IFluidHandler fluidHandlerTile = org.cyclops.evilcraft.fabric.capabilities.Capabilities.FluidHandler.BLOCK.getCapability(tile.getLevel(), tile.getBlockPos(), null, tile, null);
        if (fluidHandlerTile != null) {
            IFluidHandlerItemCapacity fluidHandlerItemCapacity = Capabilities.Item.FLUID_HANDLER_CAPACITY.getCapability(itemStack, null);
            if (fluidHandlerItemCapacity != null) {
                if (fluidHandlerTile instanceof FluidTank) {
                    FluidTank fluidTank = (FluidTank) fluidHandlerTile;
                    fluidTank.setCapacity(fluidHandlerItemCapacity.getCapacity());
                }
            }

            IFluidHandlerItem fluidHandlerItem = org.cyclops.evilcraft.fabric.capabilities.Capabilities.FluidHandler.ITEM.getCapability(itemStack, null);
            if (fluidHandlerItem != null) {
                FluidUtil.tryEmptyContainer(itemStack, fluidHandlerTile, Integer.MAX_VALUE, null, true);
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        // Force allow shift-right clicking with a fluid container passing through to this block
        if (!event.getItemStack().isEmpty()
                && org.cyclops.evilcraft.fabric.capabilities.Capabilities.FluidHandler.ITEM.getCapability(event.getItemStack(), null) != null
                && event.getLevel().getBlockState(event.getPos()).getBlock() instanceof IBlockTank) {
            event.setUseBlock(TriState.TRUE);
        }
    }

    public static class SimulatableTankWrapper implements IFluidHandler {

        private final IFluidHandler tank;

        public SimulatableTankWrapper(IFluidHandler tank) {
            this.tank = tank;
        }

        @Override
        public int getTanks() {
            return this.tank.getTanks();
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return this.tank.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return this.tank.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return this.tank.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return tank.fill(resource, action);
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            FluidStack drained = tank.drain(resource, action);
            return action.execute() ? drained : FluidContainerItemWrapperWithSimulation.asSimulatedFluidStack(new FluidStack(drained.getFluid(), drained.getAmount()));
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            FluidStack drained = tank.drain(maxDrain, action);
            return action.execute() ? drained : FluidContainerItemWrapperWithSimulation.asSimulatedFluidStack(new FluidStack(drained.getFluid(), drained.getAmount()));
        }

        public boolean isFull() {
            for (int i = 0; i < tank.getTanks(); i++) {
                if (tank.getFluidInTank(i).getAmount() < tank.getTankCapacity(i)) {
                    return false;
                }
            }
            return true;
        }

        public boolean isEmpty() {
            for (int i = 0; i < tank.getTanks(); i++) {
                if (!tank.getFluidInTank(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }


}

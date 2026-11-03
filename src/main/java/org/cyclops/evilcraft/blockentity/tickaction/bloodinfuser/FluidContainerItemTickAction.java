package org.cyclops.evilcraft.blockentity.tickaction.bloodinfuser;

import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.commons.lang3.mutable.MutableInt;
import org.cyclops.evilcraft.fabric.cyclopscore.helper.IFluidHelpersNeoForge;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.evilcraft.blockentity.BlockEntityBloodInfuser;
import org.cyclops.evilcraft.blockentity.tickaction.EmptyFluidContainerInTankTickAction;
import org.cyclops.evilcraft.core.blockentity.tickaction.ITickAction;
import org.cyclops.evilcraft.core.blockentity.upgrade.UpgradeSensitiveEvent;
import org.cyclops.evilcraft.core.blockentity.upgrade.Upgrades;

/**
 * {@link ITickAction} that can fill fluid containers with blood.
 * @author rubensworks
 *
 */
public class FluidContainerItemTickAction extends BloodInfuserTickAction{

    @Override
    public boolean canTick(BlockEntityBloodInfuser tile, ItemStack itemStack, int slot, int tick) {
        return super.canTick(tile, itemStack, slot, tick) && Capabilities.Fluid.ITEM.getCapability(itemStack, ItemAccess.forStack(itemStack)) != null;
    }

    @Override
    public void onTick(BlockEntityBloodInfuser tile, ItemStack itemStack, int slot, int tick) {
        ItemStack infuseStack = getInfuseStack(tile);
        ItemAccess itemAccessInfuse = getInfuseItemAccess(tile).oneByOne();
        ResourceHandler<FluidResource> container = Capabilities.Fluid.ITEM.getCapability(itemStack, itemAccessInfuse);
        FluidStack fluidStack = tile.getTank().getFluid().copy();

        MutableInt duration = new MutableInt(MB_PER_TICK);
        Upgrades.sendEvent(tile, new UpgradeSensitiveEvent<MutableInt>(duration, BlockEntityBloodInfuser.UPGRADEEVENT_FILLBLOODPERTICK));
        int minAmount = duration.getValue();

        fluidStack.setAmount(Math.min(minAmount, fluidStack.getAmount()));
        int filled;
        try (var tx = Transaction.openRoot()) {
            filled = container.insert(FluidResource.of(fluidStack), fluidStack.getAmount(), tx);
            tx.commit();
        }
        if (filled > 0) {
            // Everything ok, filling the container bit by bit
            try (var tx = Transaction.openRoot()) {
                tile.getTank().extract(FluidResource.of(fluidStack), filled, tx);
                tx.commit();
            }
            tile.getInventory().setItem(tile.getTileWorkingMetadata().getConsumeSlot(), infuseStack);
            if (!IModHelpers.get().getFluidHelpers().getFluid(container).isEmpty() && IModHelpers.get().getFluidHelpers().getAmount(IModHelpers.get().getFluidHelpers().getFluid(container)) == IModHelpers.get().getFluidHelpers().getCapacity(container)) {
                if (addToProduceSlot(tile, infuseStack)) {
                    tile.getInventory().removeItem(tile.getTileWorkingMetadata().getConsumeSlot(), 1);
                }
            }
        } else {
            // We might be dealing with a bucket
            FluidStack moved = IModHelpers.get().getFluidHelpers().move(tile.getTank(), container, Integer.MAX_VALUE, null, false, false);
            if (!moved.isEmpty()) {
                ItemStack result = itemAccessInfuse.getResource().toStack(itemAccessInfuse.getAmount());
                if (addToProduceSlot(tile, result)) {
                    tile.getInventory().removeItem(tile.getTileWorkingMetadata().getConsumeSlot(), 1);
                }
            }
        }
    }

    @Override
    public int getUnmodifiedRequiredTicks(BlockEntityBloodInfuser tile, int slot) {
        return EmptyFluidContainerInTankTickAction.getRequiredTicks(tile, getInfuseStack(tile));
    }

    @Override
    public ItemStack willProduceItem(BlockEntityBloodInfuser tile) {
        ItemStack itemStack = tile.getInventory().getItem(tile.getTileWorkingMetadata().getConsumeSlot()).copy(); // Copy, so we don't modify the stack
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemAccess itemAccess = ItemAccess.forStack(itemStack);
        ResourceHandler<FluidResource> itemStackHandler = Capabilities.Fluid.ITEM.getCapability(itemStack, itemAccess);
        if (itemStackHandler == null) {
            return ItemStack.EMPTY;
        }
        IFluidHelpersNeoForge fh = IModHelpers.get().getFluidHelpers();
        FluidStack moved = fh.move(tile.getTank(), itemStackHandler, MB_PER_TICK, null, false, true);
        if (moved.isEmpty()) {
            fh.move(tile.getTank(), itemStackHandler, MB_PER_TICK, null, false, true);
        }
        return itemAccess.getResource().toStack(itemAccess.getAmount());
    }

}

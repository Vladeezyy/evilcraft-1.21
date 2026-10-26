package org.cyclops.evilcraft.fabric.cyclopscore.helper;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.cyclopscore.datastructure.Wrapper;
import org.cyclops.cyclopscore.inventory.PlayerExtendedInventoryIterator;
import org.cyclops.evilcraft.fabric.cyclopscore.Capabilities;
import org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid.IFluidHandlerItemCapacity;
import org.cyclops.evilcraft.fabric.fluids.FluidActionResult;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.FluidUtil;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;

/**
 * Helpers for fluids.
 * @author rubensworks
 */
public final class FluidHelpers {

    public static final int BUCKET_VOLUME = 1000;

    public static int getAmount(FluidStack fluidStack) {
        return fluidStack.getAmount();
    }

    public static FluidStack copy(FluidStack fluidStack) {
        if(fluidStack.isEmpty()) return FluidStack.EMPTY;
        return fluidStack.copy();
    }

    public static boolean canCompletelyFill(IFluidHandler source, IFluidHandler destination) {
        FluidStack drained = source.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        return !drained.isEmpty() && destination.fill(drained, IFluidHandler.FluidAction.SIMULATE) == drained.getAmount();
    }

    public static FluidStack getFluid(@Nullable IFluidHandler fluidHandler) {
        return fluidHandler != null ? fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE) : FluidStack.EMPTY;
    }

    public static boolean hasFluid(@Nullable IFluidHandler fluidHandler) {
        return !getFluid(fluidHandler).isEmpty();
    }

    public static int getCapacity(@Nullable IFluidHandler fluidHandler) {
        int capacity = 0;
        if (fluidHandler != null) {
            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                capacity += fluidHandler.getTankCapacity(i);
            }
        }
        return capacity;
    }

    public static Optional<IFluidHandlerItemCapacity> getFluidHandlerItemCapacity(ItemStack itemStack) {
        return Optional.ofNullable(Capabilities.Item.FLUID_HANDLER_CAPACITY.getCapability(itemStack, null));
    }

    public static FluidStack extractFromInventory(int amount, @Nullable ItemStack blacklistedStack, @Nullable Fluid fluidWhitelist, Player player, IFluidHandler.FluidAction action) {
        PlayerExtendedInventoryIterator it = new PlayerExtendedInventoryIterator(player);
        Wrapper<FluidStack> drained = new Wrapper<>(FluidStack.EMPTY);
        Wrapper<Integer> amountHolder = new Wrapper<>(amount);
        while (it.hasNext() && amountHolder.get() > 0) {
            ItemStack current = it.next();
            if (current != null && current != blacklistedStack && FluidUtil.getFluidHandler(current) != null) {
                FluidUtil.getFluidHandler(current).ifPresent(fluidHandler -> {
                    FluidStack totalFluid = getFluid(fluidHandler);
                    if (!totalFluid.isEmpty() && (fluidWhitelist == null || totalFluid.getFluid() == fluidWhitelist)) {
                        FluidStack thisDrained = fluidHandler.drain(amountHolder.get(), action);
                        if (!thisDrained.isEmpty() && (fluidWhitelist == null || thisDrained.getFluid() == fluidWhitelist)) {
                            if (drained.get().isEmpty()) {
                                drained.set(thisDrained);
                            } else {
                                drained.get().setAmount(drained.get().getAmount() + thisDrained.getAmount());
                            }
                            amountHolder.set(amountHolder.get() - thisDrained.getAmount());
                        }
                    }
                });
            }
        }
        if(drained.get() != null && drained.get().getAmount() == 0) {
            drained.set(FluidStack.EMPTY);
        }
        return drained.get();
    }

    public static FluidStack extractFromItemOrInventory(int amount, ItemStack itemStack, @Nullable Player player, IFluidHandler.FluidAction action) {
        if (action.execute() && player != null && player.isCreative() && !player.level().isClientSide()) {
            action = IFluidHandler.FluidAction.SIMULATE;
        }
        if (amount == 0) return FluidStack.EMPTY;
        IFluidHandler.FluidAction finalAction = action;
        return FluidUtil.getFluidHandler(itemStack).map((fluidHandler) -> {
            FluidStack drained = fluidHandler.drain(amount, finalAction);
            if (!drained.isEmpty() && drained.getAmount() == amount) return drained;
            int drainedAmount = (drained.isEmpty() ? 0 : drained.getAmount());
            int toDrain = amount - drainedAmount;
            FluidStack otherDrained = player == null ? null : extractFromInventory(toDrain, itemStack,
                    getFluid(fluidHandler).getFluid(), player, finalAction);
            if (otherDrained == null) return drained;
            otherDrained.setAmount(otherDrained.getAmount() + drainedAmount);
            return otherDrained;
        }).orElse(FluidStack.EMPTY);
    }

    public static void placeOrPickUpFluid(Player player, InteractionHand hand, Level world, BlockPos blockPos, Direction side) {
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack itemStackResult = FluidHelpers.getFluidHandlerItemCapacity(itemStack).map(fluidHandler -> {
            FluidStack fluidStack = FluidUtil.getFluidContained(itemStack).orElse(FluidStack.EMPTY);
            FluidStack drained = fluidHandler.drain(FluidHelpers.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);

            // Try picking up a fluid if we have space in the container
            if (fluidStack.isEmpty() || (fluidStack.getAmount() + FluidHelpers.BUCKET_VOLUME <= fluidHandler.getCapacity())) {
                FluidActionResult resultPickUp = FluidUtil.tryPickUpFluid(itemStack, player, world, blockPos, side);
                if (resultPickUp.isSuccess()) {
                    return resultPickUp.getResult();
                }
            }

            // Try placing a fluid if we have something container
            if (!drained.isEmpty() && (drained.getAmount() > 0)) {
                FluidActionResult resultPlace = FluidUtil.tryPlaceFluid(player, world, hand, blockPos, itemStack, fluidStack);
                if (resultPlace.isSuccess()) {
                    return resultPlace.getResult();
                }
            }

            return itemStack;
        }).orElse(itemStack);
        player.setItemInHand(hand, itemStackResult);
    }

    public static IFluidHandler.FluidAction simulateBooleanToAction(boolean simulate) {
        return simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
    }
}

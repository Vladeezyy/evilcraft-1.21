package org.cyclops.evilcraft.fabric.fluids;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandler;
import org.cyclops.evilcraft.fabric.fluids.capability.IFluidHandlerItem;
import org.cyclops.evilcraft.fabric.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Fluid container helpers (filling and emptying item containers, moving fluids between handlers).
 */
public final class FluidUtil {

    private FluidUtil() {
    }

    public static Optional<IFluidHandlerItem> getFluidHandler(ItemStack itemStack) {
        return Optional.ofNullable(Capabilities.FluidHandler.ITEM.getCapability(itemStack, null));
    }

    public static Optional<IFluidHandler> getFluidHandler(Level level, BlockPos blockPos, @Nullable Direction side) {
        return Optional.ofNullable(Capabilities.FluidHandler.BLOCK.getCapability(level, blockPos, side));
    }

    public static Optional<FluidStack> getFluidContained(ItemStack container) {
        if (container.isEmpty()) {
            return Optional.empty();
        }
        return getFluidHandler(container.copyWithCount(1))
                .map(handler -> handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE))
                .filter(stack -> !stack.isEmpty());
    }

    private static void playSound(@Nullable Player player, FluidStack fluid, boolean fill) {
        if (player == null || fluid.isEmpty()) {
            return;
        }
        SoundEvent sound = fill ? FluidVariantAttributes.getFillSound(fluid.toVariant()) : FluidVariantAttributes.getEmptySound(fluid.toVariant());
        if (sound != null) {
            player.level().playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static FluidActionResult tryFillContainer(ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable Player player, boolean doFill) {
        // Work on a copy of a single container, the given stack is not changed
        Optional<IFluidHandlerItem> handlerOptional = getFluidHandler(container.copyWithCount(1));
        if (handlerOptional.isEmpty()) {
            return FluidActionResult.FAILURE;
        }
        IFluidHandlerItem containerHandler = handlerOptional.get();
        FluidStack simulated = tryFluidTransfer(containerHandler, fluidSource, maxAmount, false);
        if (simulated.isEmpty()) {
            return FluidActionResult.FAILURE;
        }
        if (doFill) {
            tryFluidTransfer(containerHandler, fluidSource, maxAmount, true);
            playSound(player, simulated, true);
        } else {
            // Only the copy changes, so getContainer() gives the would-be result
            containerHandler.fill(simulated, IFluidHandler.FluidAction.EXECUTE);
        }
        return new FluidActionResult(containerHandler.getContainer());
    }

    public static FluidActionResult tryEmptyContainer(ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable Player player, boolean doDrain) {
        Optional<IFluidHandlerItem> handlerOptional = getFluidHandler(container.copyWithCount(1));
        if (handlerOptional.isEmpty()) {
            return FluidActionResult.FAILURE;
        }
        IFluidHandlerItem containerHandler = handlerOptional.get();
        FluidStack transferred = tryFluidTransfer(fluidDestination, containerHandler, maxAmount, doDrain);
        if (transferred.isEmpty()) {
            return FluidActionResult.FAILURE;
        }
        if (doDrain) {
            playSound(player, transferred, false);
        } else {
            containerHandler.drain(transferred, IFluidHandler.FluidAction.EXECUTE);
        }
        return new FluidActionResult(containerHandler.getContainer());
    }

    /**
     * Fill or empty the held container with the given handler. A filled container that came from a stack of more than
     * one goes into the player's inventory (or is dropped).
     */
    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, IFluidHandler handler) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return false;
        }
        FluidActionResult result = tryFillContainerAndStow(held, handler, player, true);
        if (!result.isSuccess()) {
            result = tryEmptyContainerAndStow(held, handler, player, false);
        }
        if (result.isSuccess()) {
            player.setItemInHand(hand, result.getResult());
            return true;
        }
        return false;
    }

    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, Level level, BlockPos pos, @Nullable Direction side) {
        return getFluidHandler(level, pos, side).map(handler -> interactWithFluidHandler(player, hand, handler)).orElse(false);
    }

    private static FluidActionResult tryFillContainerAndStow(ItemStack held, IFluidHandler handler, Player player, boolean fill) {
        FluidActionResult simulated = fill
                ? tryFillContainer(held, handler, Integer.MAX_VALUE, player, false)
                : tryEmptyContainer(held, handler, Integer.MAX_VALUE, player, false);
        if (!simulated.isSuccess()) {
            return FluidActionResult.FAILURE;
        }
        if (player.getAbilities().instabuild) {
            // Creative players keep their container unchanged
            FluidActionResult done = fill
                    ? tryFillContainer(held, handler, Integer.MAX_VALUE, player, true)
                    : tryEmptyContainer(held, handler, Integer.MAX_VALUE, player, true);
            return done.isSuccess() ? new FluidActionResult(held) : FluidActionResult.FAILURE;
        }
        if (held.getCount() == 1) {
            return fill
                    ? tryFillContainer(held, handler, Integer.MAX_VALUE, player, true)
                    : tryEmptyContainer(held, handler, Integer.MAX_VALUE, player, true);
        }
        FluidActionResult done = fill
                ? tryFillContainer(held, handler, Integer.MAX_VALUE, player, true)
                : tryEmptyContainer(held, handler, Integer.MAX_VALUE, player, true);
        if (!done.isSuccess()) {
            return FluidActionResult.FAILURE;
        }
        if (!player.getInventory().add(done.getResult())) {
            player.drop(done.getResult(), false);
        }
        ItemStack remaining = held.copy();
        remaining.shrink(1);
        return new FluidActionResult(remaining);
    }

    private static FluidActionResult tryEmptyContainerAndStow(ItemStack held, IFluidHandler handler, Player player, boolean ignored) {
        return tryFillContainerAndStow(held, handler, player, false);
    }

    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer) {
        FluidStack drainable = fluidSource.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
        if (drainable.isEmpty()) {
            return FluidStack.EMPTY;
        }
        return tryFluidTransfer(fluidDestination, fluidSource, drainable, doTransfer);
    }

    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack resource, boolean doTransfer) {
        FluidStack drainable = fluidSource.drain(resource, IFluidHandler.FluidAction.SIMULATE);
        if (drainable.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, drainable)) {
            return FluidStack.EMPTY;
        }
        int fillable = fluidDestination.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
        if (fillable <= 0) {
            return FluidStack.EMPTY;
        }
        FluidStack toDrain = drainable.copyWithAmount(fillable);
        if (!doTransfer) {
            return fluidSource.drain(toDrain, IFluidHandler.FluidAction.SIMULATE);
        }
        FluidStack drained = fluidSource.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) {
            return FluidStack.EMPTY;
        }
        int filled = fluidDestination.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        if (filled < drained.getAmount()) {
            // Should not happen for well-behaved handlers: give back what did not fit
            fluidSource.fill(drained.copyWithAmount(drained.getAmount() - filled), IFluidHandler.FluidAction.EXECUTE);
            drained.setAmount(filled);
        }
        return drained;
    }

    /**
     * Fill a single empty (or not full) container with a fluid source block from the world.
     */
    public static FluidActionResult tryPickUpFluid(ItemStack emptyContainer, @Nullable Player player, Level level, BlockPos pos, @Nullable Direction side) {
        if (emptyContainer.isEmpty() || level == null || pos == null) {
            return FluidActionResult.FAILURE;
        }
        // A block with a fluid handler (a tank)
        Optional<IFluidHandler> blockHandler = getFluidHandler(level, pos, side);
        if (blockHandler.isPresent()) {
            return tryFillContainer(emptyContainer, blockHandler.get(), Integer.MAX_VALUE, player, true);
        }
        // A fluid source block
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
        net.minecraft.world.level.material.FluidState fluidState = level.getFluidState(pos);
        if (!(state.getBlock() instanceof net.minecraft.world.level.block.BucketPickup pickup) || !fluidState.isSource()) {
            return FluidActionResult.FAILURE;
        }
        FluidStack source = new FluidStack(fluidState.getType(), FluidBucketWrapper.BUCKET_VOLUME);
        Optional<IFluidHandlerItem> handlerOptional = getFluidHandler(emptyContainer.copyWithCount(1));
        if (handlerOptional.isEmpty() || handlerOptional.get().fill(source, IFluidHandler.FluidAction.SIMULATE) != source.getAmount()) {
            return FluidActionResult.FAILURE;
        }
        if (pickup.pickupBlock(player, level, pos, state).isEmpty()) {
            return FluidActionResult.FAILURE;
        }
        IFluidHandlerItem handler = handlerOptional.get();
        handler.fill(source, IFluidHandler.FluidAction.EXECUTE);
        pickup.getPickupSound().ifPresent(sound -> level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F));
        return new FluidActionResult(handler.getContainer());
    }

    /**
     * Place one bucket of the fluid of a container in the world, emptying the container by that amount.
     */
    public static FluidActionResult tryPlaceFluid(@Nullable Player player, Level level, InteractionHand hand, BlockPos pos, ItemStack container, FluidStack resource) {
        ItemStack containerCopy = container.copyWithCount(1);
        Optional<IFluidHandlerItem> handlerOptional = getFluidHandler(containerCopy);
        if (handlerOptional.isEmpty() || resource.isEmpty()) {
            return FluidActionResult.FAILURE;
        }
        IFluidHandlerItem handler = handlerOptional.get();
        FluidStack toPlace = resource.copyWithAmount(FluidBucketWrapper.BUCKET_VOLUME);
        FluidStack drainable = handler.drain(toPlace, IFluidHandler.FluidAction.SIMULATE);
        if (drainable.getAmount() < FluidBucketWrapper.BUCKET_VOLUME || !tryPlaceFluid(player, level, pos, drainable)) {
            return FluidActionResult.FAILURE;
        }
        handler.drain(toPlace, IFluidHandler.FluidAction.EXECUTE);
        return new FluidActionResult(handler.getContainer());
    }

    private static boolean tryPlaceFluid(@Nullable Player player, Level level, BlockPos pos, FluidStack fluidStack) {
        net.minecraft.world.level.material.Fluid fluid = fluidStack.getFluid();
        if (!(fluid instanceof net.minecraft.world.level.material.FlowingFluid flowingFluid)) {
            return false;
        }
        net.minecraft.world.level.block.state.BlockState destState = level.getBlockState(pos);
        boolean replaceable = destState.canBeReplaced(fluid);
        boolean container = destState.getBlock() instanceof net.minecraft.world.level.block.LiquidBlockContainer liquidContainer
                && liquidContainer.canPlaceLiquid(player, level, pos, destState, fluid);
        if (!level.isEmptyBlock(pos) && !replaceable && !container) {
            return false;
        }
        if (level.dimensionType().ultraWarm() && fluid.isSame(net.minecraft.world.level.material.Fluids.WATER)) {
            level.playSound(player, pos, net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
                    2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
            for (int i = 0; i < 8; i++) {
                level.addParticle(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
            }
            return true;
        }
        if (container) {
            ((net.minecraft.world.level.block.LiquidBlockContainer) destState.getBlock()).placeLiquid(level, pos, destState, flowingFluid.getSource(false));
        } else {
            if (!level.isClientSide && replaceable && !destState.liquid()) {
                level.destroyBlock(pos, true);
            }
            if (!level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), 11)) {
                return false;
            }
        }
        SoundEvent sound = FluidVariantAttributes.getEmptySound(fluidStack.toVariant());
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }
}

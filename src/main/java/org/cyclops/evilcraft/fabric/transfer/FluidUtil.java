package org.cyclops.evilcraft.fabric.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.sounds.SoundEvents;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * Fluid helpers with the names of NeoForge's fluid util.
 */
public final class FluidUtil {

    private FluidUtil() {
    }

    public static FluidStack getStack(ResourceHandler<FluidResource> handler, int index) {
        return handler.getResource(index).toStack(handler.getAmountAsInt(index));
    }

    public static FluidStack getFirstStackContained(ItemStack stack) {
        if (stack.isEmpty()) {
            return FluidStack.EMPTY;
        }
        ResourceHandler<FluidResource> handler = ItemAccess.forStack(stack).oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) {
            return FluidStack.EMPTY;
        }
        int size = handler.size();
        for (int index = 0; index < size; index++) {
            FluidStack fluid = getStack(handler, index);
            if (!fluid.isEmpty()) {
                return fluid;
            }
        }
        return FluidStack.EMPTY;
    }

    /**
     * Move the first fluid that can move.
     * @return What moved, empty if nothing.
     */
    public static FluidStack moveFirst(ResourceHandler<FluidResource> source, ResourceHandler<FluidResource> destination,
                                       int maxAmount, @Nullable TransactionContext transaction) {
        int size = source.size();
        for (int index = 0; index < size; index++) {
            FluidResource resource = source.getResource(index);
            if (resource.isEmpty()) {
                continue;
            }
            int accepted;
            try (Transaction test = Transaction.open(transaction)) {
                int extracted = source.extract(index, resource, maxAmount, test);
                accepted = extracted > 0 ? destination.insert(resource, extracted, test) : 0;
            }
            if (accepted > 0) {
                try (Transaction move = Transaction.open(transaction)) {
                    if (source.extract(index, resource, accepted, move) == accepted
                            && destination.insert(resource, accepted, move) == accepted) {
                        move.commit();
                        return resource.toStack(accepted);
                    }
                }
            }
        }
        return FluidStack.EMPTY;
    }

    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, Level level, BlockPos pos, @Nullable Direction side) {
        ResourceHandler<FluidResource> handler = Capabilities.Fluid.BLOCK.getCapability(level, pos, side);
        return handler != null && interactWithFluidHandler(player, hand, pos, handler);
    }

    /**
     * @param transaction Ignored here: the move is committed at once, also in NeoForge's callers that pass null.
     */
    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, Level level, BlockPos pos, @Nullable Direction side, @Nullable TransactionContext transaction) {
        return interactWithFluidHandler(player, hand, level, pos, side);
    }

    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, @Nullable BlockPos pos, ResourceHandler<FluidResource> handler) {
        ItemAccess access = ItemAccess.forPlayerInteraction(player, hand).oneByOne();
        ResourceHandler<FluidResource> handHandler = access.getCapability(Capabilities.Fluid.ITEM);
        if (handHandler == null) {
            return false;
        }
        return moveWithSound(handler, handHandler, player, pos, true) || moveWithSound(handHandler, handler, player, pos, false);
    }

    private static boolean moveWithSound(ResourceHandler<FluidResource> source, ResourceHandler<FluidResource> destination,
                                         Player player, @Nullable BlockPos pos, boolean fill) {
        FluidStack moved;
        try (Transaction transaction = Transaction.openRoot()) {
            moved = moveFirst(source, destination, Integer.MAX_VALUE, transaction);
            if (moved.isEmpty()) {
                return false;
            }
            transaction.commit();
        }
        playSound(moved, player, pos, fill);
        return true;
    }

    public static void playSound(FluidStack fluid, Player player, @Nullable BlockPos pos, boolean fill) {
        SoundEvent sound = fluid.getFluid().isSame(Fluids.LAVA)
                ? (fill ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_EMPTY_LAVA)
                : (fill ? SoundEvents.BUCKET_FILL : SoundEvents.BUCKET_EMPTY);
        BlockPos at = pos != null ? pos : player.blockPosition();
        player.level().playSound(null, at, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}

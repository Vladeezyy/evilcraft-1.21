package org.cyclops.evilcraft.fabric.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Consumer;

/**
 * Player interactions, from Fabric's use callbacks.
 */
public abstract class PlayerInteractEvent extends CancellableEvent {

    private final Player player;
    private final InteractionHand hand;
    private InteractionResult cancellationResult = InteractionResult.SUCCESS;

    protected PlayerInteractEvent(Player player, InteractionHand hand) {
        this.player = player;
        this.hand = hand;
    }

    public Player getEntity() {
        return player;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public ItemStack getItemStack() {
        return player.getItemInHand(hand);
    }

    public Level getLevel() {
        return player.level();
    }

    public InteractionResult getCancellationResult() {
        return cancellationResult;
    }

    public void setCancellationResult(InteractionResult cancellationResult) {
        this.cancellationResult = cancellationResult;
    }

    /**
     * Right clicking a block.
     */
    public static class RightClickBlock extends PlayerInteractEvent {
        private final BlockHitResult hitVec;
        private TriState useBlock = TriState.DEFAULT;

        public RightClickBlock(Player player, InteractionHand hand, BlockHitResult hitVec) {
            super(player, hand);
            this.hitVec = hitVec;
        }

        public static void addListener(Consumer<RightClickBlock> listener) {
            UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
                RightClickBlock event = new RightClickBlock(player, hand, hitResult);
                listener.accept(event);
                if (event.isCanceled()) {
                    return event.getCancellationResult();
                }
                if (event.useBlock == TriState.TRUE && player.isSecondaryUseActive()) {
                    // Vanilla skips the block when sneaking with an item, use it anyway
                    BlockState state = level.getBlockState(hitResult.getBlockPos());
                    InteractionResult itemResult = state.useItemOn(player.getItemInHand(hand), level, player, hand, hitResult);
                    if (itemResult.consumesAction()) {
                        return itemResult;
                    }
                    if (itemResult instanceof InteractionResult.TryEmptyHandInteraction && hand == InteractionHand.MAIN_HAND) {
                        InteractionResult result = state.useWithoutItem(level, player, hitResult);
                        if (result.consumesAction()) {
                            return result;
                        }
                    }
                }
                return InteractionResult.PASS;
            });
        }

        public BlockPos getPos() {
            return hitVec.getBlockPos();
        }

        public Direction getFace() {
            return hitVec.getDirection();
        }

        public BlockHitResult getHitVec() {
            return hitVec;
        }

        public void setUseBlock(TriState useBlock) {
            this.useBlock = useBlock;
        }
    }

    /**
     * Right clicking an entity.
     */
    public static class EntityInteract extends PlayerInteractEvent {
        private final Entity target;

        public EntityInteract(Player player, InteractionHand hand, Entity target) {
            super(player, hand);
            this.target = target;
        }

        public static void addListener(Consumer<EntityInteract> listener) {
            UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
                if (hitResult != null) {
                    // Only the interaction with the entity itself, not the one at a position on it
                    return InteractionResult.PASS;
                }
                EntityInteract event = new EntityInteract(player, hand, entity);
                listener.accept(event);
                return event.isCanceled() ? event.getCancellationResult() : InteractionResult.PASS;
            });
        }

        public Entity getTarget() {
            return target;
        }
    }
}

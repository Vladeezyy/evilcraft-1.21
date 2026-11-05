package org.cyclops.evilcraft.fabric.extensions;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Calls the item hooks of {@link IItemUseFirst} and {@link IItemLeftClickEntity} from Fabric's player callbacks.
 */
public final class ItemExtensions {

    private ItemExtensions() {
    }

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof IItemUseFirst item) || player.isSpectator()) {
                return InteractionResult.PASS;
            }
            InteractionResult result = item.onItemUseFirst(stack, new UseOnContext(player, hand, hitResult));
            if (result != InteractionResult.PASS) {
                return result;
            }
            if (player.isSecondaryUseActive() && item.doesSneakBypassUse(stack, level, hitResult.getBlockPos(), player)) {
                // Vanilla skips the block when sneaking with an item, use it anyway
                BlockState state = level.getBlockState(hitResult.getBlockPos());
                InteractionResult itemResult = state.useItemOn(stack, level, player, hand, hitResult);
                if (itemResult.consumesAction()) {
                    return itemResult;
                }
                if (itemResult instanceof InteractionResult.TryEmptyHandInteraction) {
                    InteractionResult blockResult = state.useWithoutItem(level, player, hitResult);
                    if (blockResult.consumesAction()) {
                        return blockResult;
                    }
                }
            }
            return InteractionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof IItemLeftClickEntity item && item.onLeftClickEntity(stack, player, entity)) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }
}

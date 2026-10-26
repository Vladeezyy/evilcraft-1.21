package org.cyclops.evilcraft.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Remembers the crafting player while the crafting result is taken (the remaining items are computed then).
 */
@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin {
    @Inject(method = "onTake", at = @At("HEAD"))
    private void evilcraft$onTakeHead(Player player, ItemStack stack, CallbackInfo ci) {
        CommonHooks.setCraftingPlayer(player);
    }

    @Inject(method = "onTake", at = @At("RETURN"))
    private void evilcraft$onTakeReturn(Player player, ItemStack stack, CallbackInfo ci) {
        CommonHooks.setCraftingPlayer(null);
    }
}

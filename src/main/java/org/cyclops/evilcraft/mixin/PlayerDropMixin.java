package org.cyclops.evilcraft.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.extensions.IItemDroppedByPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.server.level.ServerPlayer.class)
public abstract class PlayerDropMixin {
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZLnet/minecraft/util/Prediction;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void evilcraft$droppedByPlayer(ItemStack stack, boolean dropAround, net.minecraft.util.Prediction prediction, CallbackInfoReturnable<ItemEntity> cir) {
        if (stack.getItem() instanceof IItemDroppedByPlayer item && !item.onDroppedByPlayer(stack, (Player) (Object) this)) {
            cir.setReturnValue(null);
        }
    }
}

package org.cyclops.evilcraft.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.extensions.IItemEntityUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void evilcraft$itemUpdate(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getItem();
        if (stack.getItem() instanceof IItemEntityUpdate item && item.onEntityItemUpdate(stack, self)) {
            ci.cancel();
        }
    }
}

package org.cyclops.evilcraft.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.cyclops.evilcraft.fabric.extensions.ICustomItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Items of {@link ICustomItemEntity} replace their plain item entity when it is added to the world.
 */
@Mixin(ServerLevel.class)
public abstract class LevelAddEntityMixin {
    @Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
    private void evilcraft$customItemEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getClass() == ItemEntity.class) {
            ItemStack stack = ((ItemEntity) entity).getItem();
            if (stack.getItem() instanceof ICustomItemEntity item && item.hasCustomEntity(stack)) {
                ServerLevel level = (ServerLevel) (Object) this;
                Entity custom = item.createEntity(level, entity, stack);
                if (custom != null) {
                    entity.discard();
                    cir.setReturnValue(level.addFreshEntity(custom));
                }
            }
        }
    }
}

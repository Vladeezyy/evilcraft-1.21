package org.cyclops.evilcraft.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.cyclops.evilcraft.fabric.event.LivingEvent;
import org.cyclops.evilcraft.fabric.event.LivingFallEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void evilcraft$jump(CallbackInfo ci) {
        if (!LivingEvent.LivingJumpEvent.LISTENERS.isEmpty()) {
            LivingEvent.LivingJumpEvent.LISTENERS.post(new LivingEvent.LivingJumpEvent((LivingEntity) (Object) this));
        }
    }

    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float evilcraft$fall(float fallDistance, float fallDistanceArg, float damageMultiplier, DamageSource source) {
        if (LivingFallEvent.LISTENERS.isEmpty()) {
            return fallDistance;
        }
        return LivingFallEvent.LISTENERS.post(new LivingFallEvent((LivingEntity) (Object) this, fallDistance, damageMultiplier)).getDistance();
    }
}

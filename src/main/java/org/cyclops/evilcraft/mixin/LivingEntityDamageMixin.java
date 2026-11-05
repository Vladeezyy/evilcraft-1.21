package org.cyclops.evilcraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.cyclops.evilcraft.fabric.event.LivingDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Posts {@link LivingDamageEvent} around the health damage of living entities (players have their own copy of
 * actuallyHurt).
 */
@Mixin({LivingEntity.class, Player.class})
public abstract class LivingEntityDamageMixin {

    @ModifyExpressionValue(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F", ordinal = 0))
    private float evilcraft$damagePre(float damage, net.minecraft.server.level.ServerLevel level, DamageSource source, float amount, @Share("evilcraft$damage") LocalFloatRef shared) {
        float newDamage = LivingDamageEvent.firePre((LivingEntity) (Object) this, source, damage);
        shared.set(newDamage);
        return newDamage;
    }

    @Inject(method = "actuallyHurt", at = @At("TAIL"))
    private void evilcraft$damagePost(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount, CallbackInfo ci, @Share("evilcraft$damage") LocalFloatRef shared) {
        LivingDamageEvent.firePost((LivingEntity) (Object) this, source, amount, shared.get());
    }
}

package org.cyclops.evilcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.cyclops.evilcraft.fabric.event.EntityStruckByLightningEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V"))
    private void evilcraft$entityStruckByLightning(Entity entity, ServerLevel level, LightningBolt lightning, Operation<Void> original) {
        if (!EntityStruckByLightningEvent.LISTENERS.post(new EntityStruckByLightningEvent(entity, lightning)).isCanceled()) {
            original.call(entity, level, lightning);
        }
    }
}

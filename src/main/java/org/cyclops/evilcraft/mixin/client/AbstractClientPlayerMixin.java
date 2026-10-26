package org.cyclops.evilcraft.mixin.client;

import net.minecraft.client.player.AbstractClientPlayer;
import org.cyclops.evilcraft.fabric.event.ComputeFovModifierEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    @Inject(method = "getFieldOfViewModifier", at = @At("RETURN"), cancellable = true)
    private void evilcraft$fovModifier(CallbackInfoReturnable<Float> cir) {
        if (!ComputeFovModifierEvent.LISTENERS.isEmpty()) {
            cir.setReturnValue(ComputeFovModifierEvent.LISTENERS.post(
                    new ComputeFovModifierEvent((AbstractClientPlayer) (Object) this, cir.getReturnValue())).getNewFovModifier());
        }
    }
}

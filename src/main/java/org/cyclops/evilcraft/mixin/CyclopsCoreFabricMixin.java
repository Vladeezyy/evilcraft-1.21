package org.cyclops.evilcraft.mixin;

import org.cyclops.cyclopscore.CyclopsCoreFabric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cyclops Core is initialized by EvilCraft when EvilCraft's entrypoint runs first (see ModBase#onInitialize):
 * skip Fabric's second call.
 */
@Mixin(value = CyclopsCoreFabric.class, remap = false)
public class CyclopsCoreFabricMixin {
    @Inject(method = "onInitialize", at = @At("HEAD"), cancellable = true)
    private void evilcraft$initializeOnce(CallbackInfo ci) {
        if (((CyclopsCoreFabric) (Object) this).isLoaded()) {
            ci.cancel();
        }
    }
}

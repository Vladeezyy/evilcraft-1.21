package org.cyclops.evilcraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import org.cyclops.evilcraft.fabric.extensions.IBlockEntityUpdateListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Inject(method = "handleBlockEntityData", at = @At("TAIL"))
    private void evilcraft$blockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
        // Cyclops Core's block entities react to the data they received from the server
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getBlockEntity(packet.getPos()) instanceof IBlockEntityUpdateListener blockEntity) {
            blockEntity.onUpdateReceived();
        }
    }

}

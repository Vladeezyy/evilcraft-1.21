package org.cyclops.evilcraft.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import org.cyclops.evilcraft.fabric.cyclopscore.blockentity.CyclopsBlockEntity;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.world.item.crafting.RecipeManager;
import org.cyclops.evilcraft.fabric.event.RecipesUpdatedEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow
    @Final
    private RecipeManager recipeManager;

    @Inject(method = "handleBlockEntityData", at = @At("TAIL"))
    private void evilcraft$blockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
        // Cyclops Core's block entities react to the data they received from the server
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getBlockEntity(packet.getPos()) instanceof CyclopsBlockEntity blockEntity) {
            blockEntity.onUpdateReceived();
        }
    }

    @Inject(method = "handleUpdateRecipes", at = @At("TAIL"))
    private void evilcraft$recipesUpdated(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
        RecipesUpdatedEvent.LISTENERS.post(new RecipesUpdatedEvent(this.recipeManager));
    }
}

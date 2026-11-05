package org.cyclops.evilcraftcompat.modcompat.jei;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.cyclops.evilcraftcompat.modcompat.jei.spiritfurnace.SpiritFurnaceRecipeJEI;
import org.cyclops.evilcraftcompat.network.packet.JeiSpiritFurnaceRecipePacket;

import java.util.List;

/**
 * Fabric: the server part also runs without JEI on the server (JEI is client-only there);
 * the recipes go only to clients with JEI, which register the channel ({@link JeiCompatLoaderClient}).
 * @author rubensworks
 */
public class JeiCompatLoader {

    // Created once per server and data pack load, not per joining player
    private static List<SpiritFurnaceRecipeJEI> SERVER_RECIPES;

    public static void initialize() {
        // Clients only get the recipes of the serializers that are synchronized: recipe viewers and the info book need these
        org.cyclops.evilcraft.fabric.event.ModLifecycle.afterRegistries(() -> {
            net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization.synchronizeRecipeSerializer(org.cyclops.evilcraft.RegistryEntries.RECIPESERIALIZER_BLOOD_INFUSER.get());
            net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization.synchronizeRecipeSerializer(org.cyclops.evilcraft.RegistryEntries.RECIPESERIALIZER_ENVIRONMENTAL_ACCUMULATOR.get());
        });
        PayloadTypeRegistry.clientboundPlay().register(JeiSpiritFurnaceRecipePacket.ID, JeiSpiritFurnaceRecipePacket.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(handler.getPlayer()));
        // The recipes come from loot tables, which data packs change
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> SERVER_RECIPES = null);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER_RECIPES = null);
    }

    private static void onPlayerJoin(ServerPlayer player) {
        // Send loot tables from server to client.
        if (ServerPlayNetworking.canSend(player, JeiSpiritFurnaceRecipePacket.ID)) {
            if (SERVER_RECIPES == null) {
                SERVER_RECIPES = SpiritFurnaceRecipeJEI.generateServerRecipes();
            }
            ServerPlayNetworking.send(player, new JeiSpiritFurnaceRecipePacket(SERVER_RECIPES));
        }
    }

    public static void receiveSpiritFurnaceRecipesOnClient(List<SpiritFurnaceRecipeJEI> recipes) {
        JEIEvilCraftConfig.addSpiritFurnaceRecipes(recipes);
    }
}

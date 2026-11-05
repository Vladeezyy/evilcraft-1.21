package org.cyclops.evilcraftcompat.modcompat.jei;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.cyclops.evilcraftcompat.network.packet.JeiSpiritFurnaceRecipePacket;

/**
 * Fabric: the client part of {@link JeiCompatLoader}, only with JEI.
 * @author rubensworks
 */
@Environment(EnvType.CLIENT)
public class JeiCompatLoaderClient {

    /**
     * The recipes the server synchronized, where Cyclops Core's Fabric helpers look for them. Also without JEI.
     */
    public static void initializeRecipes() {
        net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent.EVENT.register((client, recipes) ->
                org.cyclops.cyclopscore.helper.MinecraftClientHelpersFabric.RECIPE_MAP = net.minecraft.world.item.crafting.RecipeMap.create(recipes.recipes()));
    }

    public static void initialize() {
        // Handlers run on the client thread
        ClientPlayNetworking.registerGlobalReceiver(JeiSpiritFurnaceRecipePacket.ID,
                (packet, context) -> JeiCompatLoader.receiveSpiritFurnaceRecipesOnClient(packet.recipes()));
    }
}

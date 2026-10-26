package org.cyclops.evilcraft.fabric.event;

import net.minecraft.world.item.crafting.RecipeManager;

/**
 * After the client received the recipes of the server (only fired on the client).
 */
public class RecipesUpdatedEvent {

    public static final ListenerList<RecipesUpdatedEvent> LISTENERS = new ListenerList<>();

    private final RecipeManager recipeManager;

    public RecipesUpdatedEvent(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }
}

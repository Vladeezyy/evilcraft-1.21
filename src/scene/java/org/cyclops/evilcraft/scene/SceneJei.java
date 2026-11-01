package org.cyclops.evilcraft.scene;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * JEI part of {@link DevScene}, an entrypoint called only with JEI: holds the JEI runtime for the checks.
 */
public class SceneJei implements IModPlugin {

    static IJeiRuntime runtime;

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("evilcraft_scene", "jei");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    static long count(RecipeType<?> type) {
        return runtime.getRecipeManager().createRecipeLookup(type).get().count();
    }

    static void show(RecipeType<?> type) {
        runtime.getRecipesGui().showTypes(List.of(type));
    }
}

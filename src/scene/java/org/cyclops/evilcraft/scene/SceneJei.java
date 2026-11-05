package org.cyclops.evilcraft.scene;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * JEI part of {@link DevScene}, an entrypoint called only with JEI: holds the JEI runtime for the checks.
 */
public class SceneJei implements IModPlugin {

    static IJeiRuntime runtime;

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath("evilcraft_scene", "jei");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    static long count(IRecipeType<?> type) {
        return runtime.getRecipeManager().createRecipeLookup(type).get().count();
    }

    static void show(IRecipeType<?> type) {
        runtime.getRecipesGui().showTypes(List.of(type));
    }
}

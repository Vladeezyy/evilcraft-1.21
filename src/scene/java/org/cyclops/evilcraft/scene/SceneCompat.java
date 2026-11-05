package org.cyclops.evilcraft.scene;

import mezz.jei.api.recipe.types.IRecipeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cyclops.evilcraftcompat.modcompat.jei.bloodinfuser.BloodInfuserRecipeCategory;
import org.cyclops.evilcraftcompat.modcompat.jei.environmentalaccumulator.EnvironmentalAccumulatorRecipeCategory;
import org.cyclops.evilcraftcompat.modcompat.jei.sanguinaryenvironmentalaccumulator.SanguinaryEnvironmentalAccumulatorRecipeCategory;
import org.cyclops.evilcraftcompat.modcompat.jei.spiritfurnace.SpiritFurnaceRecipeCategory;
import org.cyclops.evilcraftcompat.modcompat.jei.spiritreanimator.SpiritReanimatorRecipeCategory;

import java.util.List;

/**
 * EvilCraft-Compat checks of {@link DevScene}; only loaded with JEI.
 */
final class SceneCompat {

    private static final Logger LOGGER = LogManager.getLogger("evilcraft-scene");

    private static final List<IRecipeType<?>> TYPES = List.of(BloodInfuserRecipeCategory.TYPE, EnvironmentalAccumulatorRecipeCategory.TYPE,
            SanguinaryEnvironmentalAccumulatorRecipeCategory.TYPE, SpiritReanimatorRecipeCategory.TYPE, SpiritFurnaceRecipeCategory.TYPE);

    private SceneCompat() {
    }

    static void checkJei() {
        DevScene.check("JEI runtime is available", SceneJei.runtime != null, null);
        if (SceneJei.runtime == null) {
            return;
        }
        // The Spirit Furnace recipes come from the server (loot tables), the others from the client's recipes
        for (IRecipeType<?> type : TYPES) {
            long count = SceneJei.count(type);
            LOGGER.info("[scene] JEI " + type.getUid() + ": " + count + " recipes");
            DevScene.check("JEI shows recipes of " + type.getUid(), count > 0, count);
        }
    }

    /** Opens the JEI recipes of one of EvilCraft's categories, by the path of its id. */
    static void showJei(String path) {
        SceneJei.show(TYPES.stream().filter(type -> type.getUid().getPath().equals(path)).findFirst().orElseThrow());
    }
}

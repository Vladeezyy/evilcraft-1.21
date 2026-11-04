package org.cyclops.evilcraft.fabric.cyclopscore.metadata;



import net.minecraft.world.item.crafting.RecipeType;
import org.cyclops.evilcraft.EvilCraft;

public class RegistryExportables {

    public static IRegistryExportableRegistry REGISTRY = EvilCraft._instance.getRegistryManager()
            .getRegistry(IRegistryExportableRegistry.class);

    public static void load() {
        REGISTRY.register(new RegistryExportableCraftingRecipe());
        REGISTRY.register(new RegistryExportableAbstractCookingRecipe<>(() -> RecipeType.SMELTING));
        REGISTRY.register(new RegistryExportableAbstractCookingRecipe<>(() -> RecipeType.BLASTING));
        REGISTRY.register(new RegistryExportableAbstractCookingRecipe<>(() -> RecipeType.SMOKING));
        REGISTRY.register(new RegistryExportableAbstractCookingRecipe<>(() -> RecipeType.CAMPFIRE_COOKING));
        REGISTRY.register(new RegistryExportableItemTranslationKeys());
        REGISTRY.register(new RegistryExportableFluidTranslationKeys());
    }

}

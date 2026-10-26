package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.Function;

/**
 * Config for recipe serializers.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public abstract class RecipeConfig<T extends Recipe<?>> extends ExtendedConfigForge<RecipeConfig<T>, RecipeSerializer<T>> {

    /**
     * Create a new config
     *
     * @param mod                The mod instance.
     * @param namedId            A unique name id
     * @param elementConstructor The element constructor.
     */
    public RecipeConfig(ModBase<?> mod, String namedId, Function<RecipeConfig<T>, ? extends RecipeSerializer<T>> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "recipe." + getMod().getModId() + "." + getNamedId();
    }

    // Needed for config gui
    @Override
    public String getFullTranslationKey() {
        return getTranslationKey();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.RECIPE;
    }

    @Override
    public Registry<? super RecipeSerializer<T>> getRegistry() {
        return BuiltInRegistries.RECIPE_SERIALIZER;
    }
}

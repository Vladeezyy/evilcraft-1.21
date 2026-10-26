package org.cyclops.evilcraft.client.render.model;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.evilcraft.fabric.client.model.IGeometryBakingContext;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

/**
 * Model the entangled chalice.
 * @author rubensworks
 */
public class ModelEntangledChalice implements UnbakedModel {

    @Override
    public Collection<ResourceLocation> getDependencies() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        builder.add(ModelEntangledChaliceBaked.chaliceModelName);
        builder.add(ModelEntangledChaliceBaked.gemsModelName);
        return builder.build();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {

    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState) {
        ModelEntangledChaliceBaked bakedModel = new ModelEntangledChaliceBaked();

        try {
            ModelEntangledChaliceBaked.chaliceModel = bakery.bake(ModelEntangledChaliceBaked.chaliceModelName, modelState);
            ModelEntangledChaliceBaked.gemsModel = bakery.bake(ModelEntangledChaliceBaked.gemsModelName, modelState);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bakedModel;
    }

    
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        return bake(baker, spriteGetter, modelState);
    }

}

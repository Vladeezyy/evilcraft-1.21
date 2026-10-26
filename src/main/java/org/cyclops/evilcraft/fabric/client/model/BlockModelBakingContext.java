package org.cyclops.evilcraft.fabric.client.model;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;

/**
 * The baking context of a vanilla block model.
 */
public class BlockModelBakingContext implements IGeometryBakingContext {

    private final BlockModel blockModel;

    public BlockModelBakingContext(BlockModel blockModel) {
        this.blockModel = blockModel;
    }

    @Override
    public boolean hasMaterial(String name) {
        return blockModel.hasTexture(name);
    }

    @Override
    public Material getMaterial(String name) {
        return blockModel.getMaterial(name);
    }

    @Override
    public boolean isGui3d() {
        return blockModel.getGuiLight().lightLikeBlock();
    }

    @Override
    public boolean useBlockLight() {
        return blockModel.getGuiLight().lightLikeBlock();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return blockModel.hasAmbientOcclusion();
    }

    @Override
    public ItemTransforms getTransforms() {
        return blockModel.getTransforms();
    }
}

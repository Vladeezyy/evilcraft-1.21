package org.cyclops.evilcraft.fabric.client.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;

/**
 * Model settings (textures, lighting) used when baking custom geometry.
 */
public interface IGeometryBakingContext {

    boolean hasMaterial(String name);

    Material getMaterial(String name);

    boolean isGui3d();

    boolean useBlockLight();

    boolean useAmbientOcclusion();

    ItemTransforms getTransforms();
}

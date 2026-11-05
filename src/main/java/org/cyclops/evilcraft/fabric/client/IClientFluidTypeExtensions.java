package org.cyclops.evilcraft.fabric.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;

/**
 * Textures and color of a fluid for rendering, from Fabric's fluid variant rendering.
 */
@Environment(EnvType.CLIENT)
public final class IClientFluidTypeExtensions {

    private final Fluid fluid;

    private IClientFluidTypeExtensions(Fluid fluid) {
        this.fluid = fluid;
    }

    public static IClientFluidTypeExtensions of(Fluid fluid) {
        return new IClientFluidTypeExtensions(fluid);
    }

    private static Identifier name(TextureAtlasSprite[] sprites, int index) {
        if (sprites == null || sprites.length <= index || sprites[index] == null) {
            return MissingTextureAtlasSprite.getLocation();
        }
        return sprites[index].contents().name();
    }

    public Identifier getStillTexture() {
        return name(FluidVariantRendering.getSprites(FluidVariant.of(fluid)), 0);
    }

    public Identifier getFlowingTexture() {
        return name(FluidVariantRendering.getSprites(FluidVariant.of(fluid)), 1);
    }

    public Identifier getStillTexture(FluidStack stack) {
        return name(FluidVariantRendering.getSprites(stack.toVariant()), 0);
    }

    public Identifier getFlowingTexture(FluidStack stack) {
        return name(FluidVariantRendering.getSprites(stack.toVariant()), 1);
    }

    public int getTintColor() {
        return FluidVariantRendering.getColor(FluidVariant.of(fluid)) | 0xFF000000;
    }

    public int getTintColor(FluidStack stack) {
        return FluidVariantRendering.getColor(stack.toVariant()) | 0xFF000000;
    }

    public int getTintColor(net.minecraft.world.level.material.FluidState state, net.minecraft.world.level.BlockAndTintGetter level, net.minecraft.core.BlockPos pos) {
        net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler handler =
                net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry.INSTANCE.get(state.getType());
        return handler == null ? 0xFFFFFFFF : handler.getFluidColor(level, pos, state) | 0xFF000000;
    }
}

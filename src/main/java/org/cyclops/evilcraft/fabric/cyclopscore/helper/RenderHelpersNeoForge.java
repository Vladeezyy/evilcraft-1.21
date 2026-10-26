package org.cyclops.evilcraft.fabric.cyclopscore.helper;

import org.cyclops.cyclopscore.helper.IModHelpers;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import org.cyclops.evilcraft.fabric.client.IClientFluidTypeExtensions;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

/**
 * @author rubensworks
 */
/**
 * Fluid rendering helpers.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class RenderHelpersNeoForge {

    public static final RenderHelpersNeoForge INSTANCE = new RenderHelpersNeoForge(IModHelpers.get());

    private final IModHelpers modHelpers;

    private RenderHelpersNeoForge(IModHelpers modHelpers) {
        this.modHelpers = modHelpers;
    }

    public TextureAtlasSprite getFluidIcon(Fluid fluid, Direction side) {
        return getFluidIcon(new FluidStack(fluid, 1000), side);
    }

    public TextureAtlasSprite getFluidIcon(FluidStack fluid, Direction side) {
        if(side == null) side = Direction.UP;

        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite icon = modHelpers.getRenderHelpers().getBlockTextureGetter().apply(renderProperties.getFlowingTexture(fluid));
        if(icon == null || (side == Direction.UP || side == Direction.DOWN)) {
            icon = modHelpers.getRenderHelpers().getBlockTextureGetter().apply(renderProperties.getStillTexture(fluid));
        }

        return icon;
    }

    public void renderFluidContext(FluidStack fluid, PoseStack matrixStack, Runnable render) {
        if(fluid != null && fluid.getAmount() > 0) {
            matrixStack.pushPose();

            // Make sure both sides are rendered
            RenderSystem.enableBlend();
            RenderSystem.disableCull();

            // Correct color & lighting
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // Set blockState textures
            Minecraft.getInstance().getTextureManager().bindForSetup(TextureAtlas.LOCATION_BLOCKS);

            render.run();

            RenderSystem.disableBlend();
            matrixStack.popPose();
        }
    }

    public Triple<Float, Float, Float> getFluidVertexBufferColor(FluidStack fluidStack) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        int color = renderProperties.getTintColor(fluidStack);
        return this.modHelpers.getBaseHelpers().intToRGB(color);
    }

    public int getFluidBakedQuadColor(FluidStack fluidStack) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        Triple<Float, Float, Float> colorParts = this.modHelpers.getBaseHelpers().intToRGB(renderProperties.getTintColor(fluidStack));
        return this.modHelpers.getBaseHelpers().RGBAToInt(
                (int) (colorParts.getRight() * 255),
                (int) (colorParts.getMiddle() * 255),
                (int) (colorParts.getLeft() * 255),
                255
        );
    }
}

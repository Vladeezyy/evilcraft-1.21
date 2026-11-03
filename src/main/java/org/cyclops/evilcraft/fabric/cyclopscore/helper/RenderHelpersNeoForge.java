package org.cyclops.evilcraft.fabric.cyclopscore.helper;

import org.cyclops.cyclopscore.helper.IModHelpers;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import net.minecraft.client.renderer.block.FluidModel;
import org.apache.commons.lang3.tuple.Triple;

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

    private FluidModel getFluidModel(Fluid fluid) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
    }

    @Override
    public TextureAtlasSprite getFluidIcon(Fluid fluid, Direction side) {
        return getFluidIcon(new FluidStack(fluid, 1000), side);
    }

    public TextureAtlasSprite getFluidIcon(FluidStack fluid, Direction side) {
        if (side == null) side = Direction.UP;
        FluidModel model = getFluidModel(fluid.getFluid());
        if (side == Direction.UP || side == Direction.DOWN) {
            return model.stillMaterial().sprite();
        }
        return model.flowingMaterial().sprite();
    }

    @Override
    public void renderFluidContext(FluidStack fluid, PoseStack matrixStack, IFluidContextRender render) {
        if (fluid != null && fluid.getAmount() > 0) {
            matrixStack.pushPose();
            render.render();
            matrixStack.popPose();
        }
    }

    public Triple<Float, Float, Float> getFluidVertexBufferColor(FluidStack fluidStack) {
        int color = getFluidTintColor(fluidStack);
        return this.modHelpers.getBaseHelpers().intToRGB(color);
    }

    public int getFluidBakedQuadColor(FluidStack fluidStack) {
        Triple<Float, Float, Float> colorParts = this.modHelpers.getBaseHelpers().intToRGB(getFluidTintColor(fluidStack));
        return this.modHelpers.getBaseHelpers().RGBAToInt(
                (int) (colorParts.getRight() * 255),
                (int) (colorParts.getMiddle() * 255),
                (int) (colorParts.getLeft() * 255),
                255
        );
    }

    private int getFluidTintColor(FluidStack fluidStack) {
        FluidModel model = getFluidModel(fluidStack.getFluid());
        FluidTintSource tintSource = model.fluidTintSource();
        return tintSource != null ? tintSource.colorAsStack(fluidStack) : -1;
    }
}

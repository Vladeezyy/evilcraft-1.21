package org.cyclops.evilcraft.fabric.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Quads with one color. NeoForge has a color field on its BakedQuad, vanilla does not, so the color travels in the tint
 * index of the material: a negative tint index other than -1 is an opaque ARGB color, which can never be a real index.
 * Blocks turn it into vertex colors, items into constant tint sources.
 */
@Environment(EnvType.CLIENT)
public final class ColoredQuads {

    private static final int NO_TINT = -1;

    private ColoredQuads() {
    }

    /**
     * @param quad A quad.
     * @param abgr The color as NeoForge's BakedColors and Cyclops Core's getFluidBakedQuadColor have it.
     * @return The quad with this color.
     */
    public static BakedQuad withColor(BakedQuad quad, int abgr) {
        return withTint(quad, 0xFF000000 | (abgr & 0xFF00) | (abgr & 0xFF) << 16 | (abgr >> 16 & 0xFF));
    }

    /**
     * @param quad A quad.
     * @return The quad without its color and tint.
     */
    public static BakedQuad withoutColor(BakedQuad quad) {
        return withTint(quad, NO_TINT);
    }

    private static BakedQuad withTint(BakedQuad quad, int tint) {
        BakedQuad.MaterialInfo info = quad.materialInfo();
        return new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(), quad.direction(),
                new BakedQuad.MaterialInfo(info.sprite(), info.layer(), info.itemRenderType(), tint, info.shade(), info.lightEmission()));
    }

    private static boolean isColor(int tint) {
        return tint < NO_TINT;
    }

    public static void emit(QuadEmitter emitter, BakedQuad quad) {
        int tint = quad.materialInfo().tintIndex();
        emitter.fromBakedQuad(quad);
        if (isColor(tint)) {
            emitter.tintIndex(NO_TINT);
            emitter.color(tint, tint, tint, tint);
        }
        emitter.emit();
    }

    /**
     * @param quads The quads of an item at this moment.
     * @param properties The render properties.
     * @return An item model with these quads.
     */
    public static ItemModel itemModel(List<BakedQuad> quads, ModelRenderProperties properties) {
        List<ItemTintSource> tints = new ArrayList<>(2);
        List<Integer> colors = new ArrayList<>(2);
        QuadCollection.Builder builder = new QuadCollection.Builder();
        for (BakedQuad quad : quads) {
            int tint = quad.materialInfo().tintIndex();
            if (isColor(tint)) {
                int index = colors.indexOf(tint);
                if (index < 0) {
                    index = colors.size();
                    colors.add(tint);
                    tints.add(new Constant(tint));
                }
                quad = withTint(quad, index);
            }
            builder.addUnculledFace(quad);
        }
        return new CuboidItemModelWrapper(tints, builder.build(), properties, new Matrix4f());
    }
}

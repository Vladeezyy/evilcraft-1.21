package org.cyclops.evilcraft.fabric.event;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

/**
 * NeoForge's event of this name: on Fabric the id mapper is filled at once, Fabric API has no hook for it.
 */
@Environment(EnvType.CLIENT)
public final class RegisterSpecialModelRendererEvent {

    public static void addListener(Consumer<RegisterSpecialModelRendererEvent> listener) {
        listener.accept(new RegisterSpecialModelRendererEvent());
    }

    public void register(Identifier id, MapCodec<? extends SpecialModelRenderer.Unbaked> codec) {
        SpecialModelRenderers.ID_MAPPER.put(id, codec);
    }
}

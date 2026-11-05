package org.cyclops.evilcraft.fabric.event;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

/**
 * NeoForge's event of this name: on Fabric the id mapper is filled at once, Fabric API has no hook for it.
 */
@Environment(EnvType.CLIENT)
public final class RegisterItemModelsEvent {

    public static void addListener(Consumer<RegisterItemModelsEvent> listener) {
        listener.accept(new RegisterItemModelsEvent());
    }

    public void register(Identifier id, MapCodec<? extends ItemModel.Unbaked> codec) {
        ItemModels.ID_MAPPER.put(id, codec);
    }
}

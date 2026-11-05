package org.cyclops.evilcraft.fabric.event;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

/**
 * NeoForge's event of this name. A blockstate file names such a model with "fabric:type".
 */
@Environment(EnvType.CLIENT)
public final class RegisterBlockStateModels {

    public static void addListener(Consumer<RegisterBlockStateModels> listener) {
        listener.accept(new RegisterBlockStateModels());
    }

    public void registerModel(Identifier id, MapCodec<? extends CustomUnbakedBlockStateModel> codec) {
        CustomUnbakedBlockStateModel.register(id, codec);
    }
}

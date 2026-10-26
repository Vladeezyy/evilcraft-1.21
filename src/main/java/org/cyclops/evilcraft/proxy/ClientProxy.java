package org.cyclops.evilcraft.proxy;

import org.cyclops.evilcraft.RegistryEntries;
import net.minecraft.client.renderer.RenderType;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import org.cyclops.evilcraft.fabric.client.ModelRenderTypes;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.RegisterKeyMappingsEvent;
import java.util.List;
import org.cyclops.evilcraft.fabric.client.model.ModelLoaders;
import org.cyclops.evilcraft.fabric.event.RenderGuiEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;
import org.cyclops.evilcraft.fabric.cyclopscore.proxy.ClientProxyComponent;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.Reference;
import org.cyclops.evilcraft.client.key.ExaltedCrafterKeyHandler;
import org.cyclops.evilcraft.client.key.FartKeyHandler;
import org.cyclops.evilcraft.client.key.Keys;
import org.cyclops.evilcraft.client.render.model.ModelBoxOfEternalClosure;
import org.cyclops.evilcraft.client.render.model.ModelEntangledChaliceBaked;
import org.cyclops.evilcraft.core.broom.BroomParts;
import org.cyclops.evilcraft.core.client.model.ModelLoaderBoxOfEternalClosure;
import org.cyclops.evilcraft.core.client.model.ModelLoaderBroom;
import org.cyclops.evilcraft.core.client.model.ModelLoaderBroomPart;
import org.cyclops.evilcraft.core.client.model.ModelLoaderDarkTank;
import org.cyclops.evilcraft.core.client.model.ModelLoaderDisplayStand;
import org.cyclops.evilcraft.core.client.model.ModelLoaderEntangledChalice;
import org.cyclops.evilcraft.event.RenderOverlayEventHook;

/**
 * Proxy for the client side.
 *
 * @author rubensworks
 *
 */
public class ClientProxy extends ClientProxyComponent {

    public ClientProxy() {
        super(new CommonProxy());
        this.onModelLoad();
        this.onModelRegisterAdditional();
        ModelLoaders.init(Reference.MOD_ID);
        ModelRenderTypes.init(Reference.MOD_ID);
    }

    @Override
    public void registerRenderers() {
        super.registerRenderers();
        // The fluid rendered inside the tank model
        BlockRenderLayerMap.INSTANCE.putBlock(RegistryEntries.BLOCK_DARK_TANK.get(), RenderType.translucent());
    }

    @Override
    public ModBase getMod() {
        return EvilCraft._instance;
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry, RegisterKeyMappingsEvent event) {
        Options settings = Minecraft.getInstance().options;

        for (KeyMapping key : Keys.KEYS)
            event.register(key);

        // Fart key
        FartKeyHandler fartKeyHandler = new FartKeyHandler();

        keyRegistry.addKeyHandler(Keys.FART, fartKeyHandler);
        keyRegistry.addKeyHandler(Keys.EXALTEDCRAFTING, new ExaltedCrafterKeyHandler());
        keyRegistry.addKeyHandler(settings.keyShift, fartKeyHandler);

        EvilCraft.clog("Registered key bindings");
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();

        RenderGuiEvent.Post.addListener(new RenderOverlayEventHook()::onRenderOverlayEvent);
    }

    public void onModelLoad() {
        ModelLoaders.registerLoader(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "broom"), new ModelLoaderBroom());
        ModelLoaders.registerLoader(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "broom_part"), new ModelLoaderBroomPart());
        ModelLoaders.registerLoader(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "box_of_eternal_closure"), new ModelLoaderBoxOfEternalClosure());
        ModelLoaders.registerLoader(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "dark_tank"), new ModelLoaderDarkTank());
        ModelLoaders.registerLoader(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "entangled_chalice"), new ModelLoaderEntangledChalice());
        ModelLoaders.registerLoader(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "display_stand"), new ModelLoaderDisplayStand());
    }

    public void onModelRegisterAdditional() {
        // Box of eternal closure
        ModelLoaders.registerAdditionalModels(() -> List.of(ModelBoxOfEternalClosure.boxModel,
                ModelBoxOfEternalClosure.boxLidModel, ModelBoxOfEternalClosure.boxLidRotatedModel));
        // Broom
        ModelLoaders.registerAdditionalModels(BroomParts.REGISTRY::getPartModels);
        // Entangled chalice
        ModelLoaders.registerAdditionalModels(() -> List.of(ModelEntangledChaliceBaked.chaliceModelName,
                ModelEntangledChaliceBaked.gemsModelName));
    }

}

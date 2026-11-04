package org.cyclops.evilcraft.proxy;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.RegisterKeyMappingsEvent;
import org.cyclops.evilcraft.fabric.event.RenderGuiEvent;
import net.minecraft.client.KeyMapping;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.evilcraft.fabric.cyclopscore.proxy.ClientProxyComponent;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.block.BlockEntangledChaliceConfig;
import org.cyclops.evilcraft.client.key.ExaltedCrafterKeyHandler;
import org.cyclops.evilcraft.client.key.FartKeyHandler;
import org.cyclops.evilcraft.client.key.Keys;
import org.cyclops.evilcraft.client.render.blockentity.*;
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
    }

    @Override
    public ModBaseNeoForge<EvilCraft> getMod() {
        return EvilCraft._instance;
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry, RegisterKeyMappingsEvent event) {
        for (KeyMapping key : Keys.KEYS)
            event.register(key);

        // Fart key
        FartKeyHandler fartKeyHandler = new FartKeyHandler();

        keyRegistry.addKeyHandler(Keys.FART, fartKeyHandler);
        keyRegistry.addKeyHandler(Keys.EXALTEDCRAFTING, new ExaltedCrafterKeyHandler());
        // The options do not exist yet when Fabric runs the entrypoints
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> keyRegistry.addKeyHandler(client.options.keyShift, fartKeyHandler));

        EvilCraft.clog("Registered key bindings");
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();

        RenderGuiEvent.Post.addListener(new RenderOverlayEventHook()::onRenderOverlayEvent);
    }

    @Override
    public void registerRenderers() {
        registerRenderer(RegistryEntries.BLOCK_ENTITY_BLOOD_CHEST.get(), RenderBlockEntityBloodChest::new);
        registerRenderer(RegistryEntries.BLOCK_ENTITY_BOX_OF_ETERNAL_CLOSURE.get(), RenderBlockEntityBoxOfEternalClosure::new);
        registerRenderer(RegistryEntries.BLOCK_ENTITY_COLOSSAL_BLOOD_CHEST.get(), RenderBlockEntityColossalBloodChest::new);
        registerRenderer(RegistryEntries.BLOCK_ENTITY_DARK_TANK.get(), RenderBlockEntityDarkTank::new);
        registerRenderer(RegistryEntries.BLOCK_ENTITY_DISPLAY_STAND.get(), RenderBlockEntityDisplayStand::new);
        if (!BlockEntangledChaliceConfig.staticBlockRendering) {
            registerRenderer(RegistryEntries.BLOCK_ENTITY_ENTANGLED_CHALICE.get(), RenderBlockEntityEntangledChalice::new);
        }
        registerRenderer(RegistryEntries.BLOCK_ENTITY_ENVIRONMENTAL_ACCUMULATOR.get(), RenderBlockEntityEnvironmentalAccumulator::new);
        registerRenderer(RegistryEntries.BLOCK_ENTITY_PURIFIER.get(), RenderBlockEntityPurifier::new);
        registerRenderer(RegistryEntries.BLOCK_ENTITY_SPIRIT_PORTAL.get(), RenderBlockEntitySpiritPortal::new);

        super.registerRenderers();
    }

}

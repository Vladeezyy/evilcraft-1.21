package org.cyclops.evilcraft.fabric.cyclopscore.proxy;

import org.cyclops.cyclopscore.proxy.ICommonProxyFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.RegisterKeyMappingsEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBaseNeoForge;

/**
 * Interface for common proxies.
 * @author rubensworks
 */
public interface ICommonProxy extends ICommonProxyFabric {

    /**
     * @return The mod for this proxy.
     */
    @Override
    public ModBaseNeoForge getMod();

    /**
     * Register key bindings (client only).
     * @param keyRegistry The key registry to register to.
     * @param event Register mappings event
     */
    public void registerKeyBindings(IKeyRegistry keyRegistry, RegisterKeyMappingsEvent event);

    public default void registerKeyBindings(IKeyRegistry keyRegistry) {
        registerKeyBindings(keyRegistry, new RegisterKeyMappingsEvent());
    }

}

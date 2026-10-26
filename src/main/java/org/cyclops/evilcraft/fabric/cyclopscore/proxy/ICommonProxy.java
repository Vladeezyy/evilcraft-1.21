package org.cyclops.evilcraft.fabric.cyclopscore.proxy;

import org.cyclops.cyclopscore.proxy.ICommonProxyFabric;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.RegisterKeyMappingsEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;
import org.cyclops.evilcraft.fabric.cyclopscore.network.PacketHandler;

/**
 * Interface for common proxies.
 * @author rubensworks
 */
public interface ICommonProxy extends ICommonProxyFabric {

    /**
     * @return The mod for this proxy.
     */
    @Override
    public ModBase getMod();

    /**
     * Register key bindings (client only).
     * @param keyRegistry The key registry to register to.
     * @param event Register mappings event
     */
    public void registerKeyBindings(IKeyRegistry keyRegistry, RegisterKeyMappingsEvent event);

    public default void registerKeyBindings(IKeyRegistry keyRegistry) {
        registerKeyBindings(keyRegistry, new RegisterKeyMappingsEvent());
    }

    /**
     * Register packet handlers.
     * @param packetHandler The packet handler.
     */
    public void registerPacketHandlers(PacketHandler packetHandler);

}

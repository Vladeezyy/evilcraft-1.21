package org.cyclops.evilcraft.fabric.cyclopscore.proxy;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.cyclops.cyclopscore.proxy.ClientProxyComponentCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.cyclopscore.network.IPacketHandler;

/**
 * Base proxy for the client side.
 * @author rubensworks
 */
public abstract class ClientProxyComponent extends ClientProxyComponentCommon implements IClientProxy {

    private final CommonProxyComponent commonProxyComponent;

    public ClientProxyComponent(CommonProxyComponent commonProxyComponent) {
        super(commonProxyComponent);
        this.commonProxyComponent = commonProxyComponent;
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry, RegisterKeyMappingsEvent event) {
        getMod().getLoggerHelper().log(Level.TRACE, "Registered key bindings");
        event.registerCategory(getMainKeyCategory());
    }

    @Override
    public void registerPackets(IPacketHandler packetHandler) {
        getCommonProxyComponent().registerPackets(packetHandler);
        getMod().getLoggerHelper().log(Level.TRACE, "Registered packet handlers");
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();
        ClientTickEvents.END_CLIENT_TICK.register(client -> getMod().getKeyRegistry().onPlayerKeyInput());
    }
}

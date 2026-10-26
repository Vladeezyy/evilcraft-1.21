package org.cyclops.evilcraft.fabric.cyclopscore.proxy;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.cyclops.cyclopscore.proxy.ClientProxyComponentCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.RegisterKeyMappingsEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.network.PacketHandler;

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

    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        commonProxyComponent.registerPacketHandlers(packetHandler);
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();
        ClientTickEvents.END_CLIENT_TICK.register(client -> getMod().getKeyRegistry().onPlayerKeyInput());
    }
}

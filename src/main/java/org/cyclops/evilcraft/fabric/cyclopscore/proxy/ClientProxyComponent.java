package org.cyclops.evilcraft.fabric.cyclopscore.proxy;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.proxy.ClientProxyComponentCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.RegisterKeyMappingsEvent;

/**
 * Base proxy for the client side.
 * @author rubensworks
 */
public abstract class ClientProxyComponent extends ClientProxyComponentCommon implements IClientProxy {

    public ClientProxyComponent(CommonProxyComponent commonProxyComponent) {
        super(commonProxyComponent);
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry, RegisterKeyMappingsEvent event) {
        getMod().getLoggerHelper().log(Level.TRACE, "Registered key bindings");
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();
        ClientTickEvents.END_CLIENT_TICK.register(client -> getMod().getKeyRegistry().onPlayerKeyInput());
    }
}

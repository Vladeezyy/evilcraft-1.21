package org.cyclops.evilcraft.fabric.cyclopscore.proxy;

import org.cyclops.cyclopscore.proxy.CommonProxyComponentCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.IKeyRegistry;
import org.cyclops.evilcraft.fabric.cyclopscore.client.key.RegisterKeyMappingsEvent;

/**
 * Base proxy for server and client side.
 * @author rubensworks
 */
public abstract class CommonProxyComponent extends CommonProxyComponentCommon implements ICommonProxy {

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry, RegisterKeyMappingsEvent event) {

    }

}

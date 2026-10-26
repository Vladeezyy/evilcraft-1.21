package org.cyclops.evilcraft.item;

import net.minecraft.world.item.Item;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.event.RegisterColorHandlersEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for the {@link ItemPromiseAcceptor}.
 * @author rubensworks
 *
 */
public class ItemPromiseAcceptorConfig extends ItemConfig {

    public ItemPromiseAcceptorConfig(ItemPromiseAcceptor.Type type) {
        super(
                EvilCraft._instance,
                "promise_acceptor_" + type.getName(),
                eConfig -> new ItemPromiseAcceptor(new Item.Properties()
                        , type)
        );
        if (MinecraftHelpers.isClientSide()) {
            RegisterColorHandlersEvent.Item.addListener(this::registerColors);
        }
    }

    @Environment(EnvType.CLIENT)
    public void registerColors(RegisterColorHandlersEvent.Item event) {
        event.register(new ItemPromiseAcceptor.ItemColor(), getInstance());
    }

}

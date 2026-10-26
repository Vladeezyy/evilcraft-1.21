package org.cyclops.evilcraft.item;

import net.minecraft.world.item.Item;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.event.RegisterColorHandlersEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for the {@link ItemBowlOfPromises}.
 * @author rubensworks
 *
 */
public class ItemBowlOfPromisesConfig extends ItemConfig {

    public ItemBowlOfPromisesConfig(ItemBowlOfPromises.Type type) {
        super(
                EvilCraft._instance,
            "bowl_of_promises_" + type.getName(),
                eConfig -> new ItemBowlOfPromises(new Item.Properties()
                        , type)
        );
        if (MinecraftHelpers.isClientSide()) {
            RegisterColorHandlersEvent.Item.addListener(this::registerColors);
        }
    }

    @Environment(EnvType.CLIENT)
    public void registerColors(RegisterColorHandlersEvent.Item event) {
        event.register(new ItemBowlOfPromises.ItemColor(), getInstance());
    }
}

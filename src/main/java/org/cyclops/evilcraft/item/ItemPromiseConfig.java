package org.cyclops.evilcraft.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.event.RegisterColorHandlersEvent;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.fabric.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.core.blockentity.upgrade.Upgrades;

/**
 * Config for the {@link ItemPromise}.
 * @author rubensworks
 *
 */
public class ItemPromiseConfig extends ItemConfig {

    public ItemPromiseConfig(Upgrades.Upgrade upgrade) {
        super(
                EvilCraft._instance,
            "promise_" + upgrade.getId() + "_" + upgrade.getTier(),
                eConfig -> new ItemPromise(new Item.Properties()
                        .component(DataComponents.RARITY, upgrade.getTier() > 0 ? Rarity.RARE : Rarity.UNCOMMON)
                        .stacksTo(upgrade.getTier() > 0 ? 1 : 4), upgrade) // All the 'tier' upgrades can only have StackSize 1.
        );
        if (MinecraftHelpers.isClientSide()) {
            RegisterColorHandlersEvent.Item.addListener(this::registerColors);
        }
    }

    @Environment(EnvType.CLIENT)
    public void registerColors(RegisterColorHandlersEvent.Item event) {
        event.register(new ItemPromise.ItemColor(), getInstance());
    }
}

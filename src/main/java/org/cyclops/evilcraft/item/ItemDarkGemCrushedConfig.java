package org.cyclops.evilcraft.item;

import org.cyclops.evilcraft.fabric.event.ModLifecycle;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for the Crushed Dark Gem.
 * @author rubensworks
 *
 */
public class ItemDarkGemCrushedConfig extends ItemConfig {

    public ItemDarkGemCrushedConfig() {
        super(
                EvilCraft._instance,
                "dark_gem_crushed",
                eConfig -> new Item(new Item.Properties()
                        )
        );
        ModLifecycle.afterRegistries(() -> FuelRegistry.INSTANCE.add(this.getInstance(), 16000));
    }


}

package org.cyclops.evilcraft.item;

import org.cyclops.evilcraft.fabric.event.ModLifecycle;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for the Blood-Waxed Coal.
 * @author rubensworks
 *
 */
public class ItemBloodWaxedCoalConfig extends ItemConfig {

    public ItemBloodWaxedCoalConfig() {
        super(
                EvilCraft._instance,
            "blood_waxed_coal",
                eConfig -> new Item(new Item.Properties()
                        )
        );
        ModLifecycle.afterRegistries(() -> FuelRegistry.INSTANCE.add(this.getInstance(), 3200));
    }


}

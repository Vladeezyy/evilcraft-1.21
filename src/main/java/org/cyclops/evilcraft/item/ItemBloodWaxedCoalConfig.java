package org.cyclops.evilcraft.item;

import org.cyclops.evilcraft.fabric.event.ModLifecycle;
import net.minecraft.world.item.Item;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for the Blood-Waxed Coal.
 * @author rubensworks
 *
 */
public class ItemBloodWaxedCoalConfig extends ItemConfigCommon<IModBase> {

    public ItemBloodWaxedCoalConfig() {
        super(
                EvilCraft._instance,
            "blood_waxed_coal",
                (eConfig, properties) -> new Item(properties)
        );
        ModLifecycle.afterRegistries(() -> net.fabricmc.fabric.api.registry.FuelRegistryEvents.BUILD.register((builder, context) -> builder.add(this.getInstance(), 3200)));
    }


}

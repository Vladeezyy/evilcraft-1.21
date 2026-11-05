package org.cyclops.evilcraft.item;

import org.cyclops.evilcraft.fabric.event.ModLifecycle;
import net.minecraft.world.item.Item;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for the Crushed Dark Gem.
 * @author rubensworks
 *
 */
public class ItemDarkGemCrushedConfig extends ItemConfigCommon<IModBase> {

    public ItemDarkGemCrushedConfig() {
        super(
                EvilCraft._instance,
                "dark_gem_crushed",
                (eConfig, properties) -> new Item(properties)
        );
        ModLifecycle.afterRegistries(() -> net.fabricmc.fabric.api.registry.FuelValueEvents.BUILD.register((builder, context) -> builder.add(this.getInstance(), 16000)));
    }


}

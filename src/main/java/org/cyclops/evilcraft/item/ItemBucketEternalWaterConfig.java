package org.cyclops.evilcraft.item;

import net.minecraft.world.item.Item;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.fluids.capability.wrappers.FluidBucketWrapper;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for the Eternal Water Bucket.
 * @author rubensworks
 *
 */
public class ItemBucketEternalWaterConfig extends ItemConfig {

    public ItemBucketEternalWaterConfig() {
        super(
                EvilCraft._instance,
            "bucket_eternal_water",
                eConfig -> new ItemBucketEternalWater(new Item.Properties()
                        )
        );
        RegisterCapabilitiesEvent.addListener(this::registerCapability);
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new FluidBucketWrapper(stack) {
            @Override
            protected void setFluid(FluidStack fluid) {
                // Do nothing: we want to keep the item in-place
            }
        }, getInstance());
    }

}

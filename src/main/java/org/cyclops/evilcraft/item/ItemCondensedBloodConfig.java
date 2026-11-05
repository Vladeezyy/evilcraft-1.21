package org.cyclops.evilcraft.item;

import net.minecraft.world.item.Item;
import org.cyclops.evilcraft.fabric.capabilities.Capabilities;
import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import org.cyclops.evilcraft.fabric.fluids.FluidStack;
import org.cyclops.evilcraft.fabric.transfer.ItemResource;
import org.cyclops.evilcraft.fabric.cyclopscore.capability.fluid.ResourceHandlerFluidSwapEmpty;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;

/**
 * Config for the condensed blood.
 * @author rubensworks
 *
 */
public class ItemCondensedBloodConfig extends ItemConfigCommon<IModBase> {

    public ItemCondensedBloodConfig() {
        super(
                EvilCraft._instance,
            "condensed_blood",
                (eConfig, properties) -> new Item(properties)
        );
        RegisterCapabilitiesEvent.addListener(this::registerCapability);
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Fluid.ITEM, (stack, context) -> new ResourceHandlerFluidSwapEmpty(
                context,
                new FluidStack(RegistryEntries.FLUID_BLOOD, 500),
                ItemResource.of(getInstance()),
                ItemResource.EMPTY
        ), getInstance());
    }

}

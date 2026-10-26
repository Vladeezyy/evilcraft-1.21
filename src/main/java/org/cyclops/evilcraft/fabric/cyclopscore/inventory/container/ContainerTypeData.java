package org.cyclops.evilcraft.fabric.cyclopscore.inventory.container;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeDataCommon;
import org.cyclops.cyclopscore.inventory.container.IContainerFactoryCommon;

/**
 * A menu type that receives extra data when opened.
 * @author rubensworks
 */
public class ContainerTypeData<T extends net.minecraft.world.inventory.AbstractContainerMenu> extends ContainerTypeDataCommon<T> {
    public ContainerTypeData(IContainerFactoryCommon<T> factory, FeatureFlagSet featureFlagSet) {
        super(factory, featureFlagSet);
    }
}

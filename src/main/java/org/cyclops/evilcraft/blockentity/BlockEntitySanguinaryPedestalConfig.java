package org.cyclops.evilcraft.blockentity;

import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.cyclops.cyclopscore.config.extendedconfig.BlockEntityConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;

/**
 * Config for the {@link BlockEntitySanguinaryPedestal}.
 * @author rubensworks
 *
 */
public class BlockEntitySanguinaryPedestalConfig extends BlockEntityConfigCommon<BlockEntitySanguinaryPedestal, IModBase> {

    public BlockEntitySanguinaryPedestalConfig() {
        super(
                EvilCraft._instance,
                "sanguinary_pedestal",
                (eConfig) -> new BlockEntityType<>(BlockEntitySanguinaryPedestal::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_SANGUINARY_PEDESTAL_0.get(), RegistryEntries.BLOCK_SANGUINARY_PEDESTAL_1.get()))
        );
        RegisterCapabilitiesEvent.addListener(new BlockEntitySanguinaryPedestal.CapabilityRegistrar(this::getInstance)::register);
    }

}

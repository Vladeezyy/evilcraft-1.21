package org.cyclops.evilcraft.blockentity;

import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockEntityConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;

/**
 * Config for the {@link BlockEntitySpiritFurnace}.
 * @author rubensworks
 *
 */
public class BlockEntitySpiritFurnaceConfig extends BlockEntityConfig<BlockEntitySpiritFurnace> {

    public BlockEntitySpiritFurnaceConfig() {
        super(
                EvilCraft._instance,
                "spirit_furnace",
                (eConfig) -> new BlockEntityType<>(BlockEntitySpiritFurnace::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_SPIRIT_FURNACE.get()), null)
        );
        RegisterCapabilitiesEvent.addListener(new BlockEntitySpiritFurnace.CapabilityRegistrar(this::getInstance)::register);
    }

}

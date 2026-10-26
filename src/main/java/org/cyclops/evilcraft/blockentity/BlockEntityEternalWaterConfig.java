package org.cyclops.evilcraft.blockentity;

import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockEntityConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;

/**
 * Config for the {@link BlockEntityEternalWater}.
 * @author rubensworks
 *
 */
public class BlockEntityEternalWaterConfig extends BlockEntityConfig<BlockEntityEternalWater> {

    public BlockEntityEternalWaterConfig() {
        super(
                EvilCraft._instance,
                "eternal_water",
                (eConfig) -> new BlockEntityType<>(BlockEntityEternalWater::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_ETERNAL_WATER.get()), null)
        );
        RegisterCapabilitiesEvent.addListener(new BlockEntityEternalWater.CapabilityRegistrar(this::getInstance)::register);
    }

}

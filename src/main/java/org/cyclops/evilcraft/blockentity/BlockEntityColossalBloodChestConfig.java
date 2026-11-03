package org.cyclops.evilcraft.blockentity;

import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.cyclops.cyclopscore.config.extendedconfig.BlockEntityConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;

/**
 * Config for the {@link BlockEntityColossalBloodChest}.
 * @author rubensworks
 *
 */
public class BlockEntityColossalBloodChestConfig extends BlockEntityConfigCommon<BlockEntityColossalBloodChest, IModBase> {

    public BlockEntityColossalBloodChestConfig() {
        super(
                EvilCraft._instance,
                "colossal_blood_chest",
                (eConfig) -> new BlockEntityType<>(BlockEntityColossalBloodChest::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_COLOSSAL_BLOOD_CHEST.get()))
        );
        RegisterCapabilitiesEvent.addListener(new BlockEntityColossalBloodChest.CapabilityRegistrar(this::getInstance)::register);
    }

}

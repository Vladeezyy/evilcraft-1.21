package org.cyclops.evilcraft.blockentity;

import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.cyclops.cyclopscore.config.ModConfigLocation;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockEntityConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;

import java.util.List;

/**
 * Config for the {@link BlockEntityBloodInfuser}.
 * @author rubensworks
 *
 */
public class BlockEntityBloodInfuserConfig extends BlockEntityConfig<BlockEntityBloodInfuser> {

    @ConfigurablePropertyCommon(category = "machine", comment = "Priority list of mod id's when determining tag-based recipe outputs.", isCommandable = true, configLocation = ModConfigLocation.SERVER)
    public static List<String> recipeTagOutputModPriorities = Lists.newArrayList();

    public BlockEntityBloodInfuserConfig() {
        super(
                EvilCraft._instance,
                "blood_infuser",
                (eConfig) -> new BlockEntityType<>(BlockEntityBloodInfuser::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_BLOOD_INFUSER.get()), null)
        );
        RegisterCapabilitiesEvent.addListener(new BlockEntityBloodInfuser.CapabilityRegistrar(this::getInstance)::register);
    }
}

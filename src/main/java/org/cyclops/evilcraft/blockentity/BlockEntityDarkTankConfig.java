package org.cyclops.evilcraft.blockentity;

import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockEntityConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.block.BlockDarkTankConfig;
import org.cyclops.evilcraft.client.render.blockentity.RenderBlockEntityDarkTank;

/**
 * Config for the {@link BlockEntityDarkTank}.
 * @author rubensworks
 *
 */
public class BlockEntityDarkTankConfig extends BlockEntityConfig<BlockEntityDarkTank> {

    public BlockEntityDarkTankConfig() {
        super(
                EvilCraft._instance,
                "dark_tank",
                (eConfig) -> new BlockEntityType<>(BlockEntityDarkTank::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_DARK_TANK.get()), null)
        );
        RegisterCapabilitiesEvent.addListener(new BlockEntityDarkTank.CapabilityRegistrar<>(this::getInstance)::register);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onRegistered() {
        super.onRegistered();
        if(!BlockDarkTankConfig.staticBlockRendering) {
            getMod().getProxy().registerRenderer(getInstance(), RenderBlockEntityDarkTank::new);
        }
    }

}

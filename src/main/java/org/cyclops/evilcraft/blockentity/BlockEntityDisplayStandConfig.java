package org.cyclops.evilcraft.blockentity;

import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockEntityConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.client.render.blockentity.RenderBlockEntityDisplayStand;

/**
 * Config for the {@link BlockEntityDisplayStand}.
 * @author rubensworks
 *
 */
public class BlockEntityDisplayStandConfig extends BlockEntityConfig<BlockEntityDisplayStand> {

    public BlockEntityDisplayStandConfig() {
        super(
                EvilCraft._instance,
                "display_stand",
                (eConfig) -> new BlockEntityType<>(BlockEntityDisplayStand::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_DISPLAY_STAND.get()), null)
        );
        RegisterCapabilitiesEvent.addListener(new BlockEntityDisplayStand.CapabilityRegistrar(this::getInstance)::register);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onRegistered() {
        super.onRegistered();
        EvilCraft._instance.getProxy().registerRenderer(getInstance(), RenderBlockEntityDisplayStand::new);
    }

}

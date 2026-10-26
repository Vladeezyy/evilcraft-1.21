package org.cyclops.evilcraft.blockentity;

import org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent;
import com.google.common.collect.Sets;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockEntityConfig;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.block.BlockEntangledChaliceConfig;
import org.cyclops.evilcraft.client.render.blockentity.RenderBlockEntityEntangledChalice;

/**
 * Config for the {@link BlockEntityEntangledChalice}.
 * @author rubensworks
 *
 */
public class BlockEntityEntangledChaliceConfig extends BlockEntityConfig<BlockEntityEntangledChalice> {

    public BlockEntityEntangledChaliceConfig() {
        super(
                EvilCraft._instance,
                "entangled_chalice",
                (eConfig) -> new BlockEntityType<>(BlockEntityEntangledChalice::new,
                        Sets.newHashSet(RegistryEntries.BLOCK_ENTANGLED_CHALICE.get()), null)
        );
        RegisterCapabilitiesEvent.addListener(new BlockEntityEntangledChalice.CapabilityRegistrar(this::getInstance)::register);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onRegistered() {
        super.onRegistered();
        if(!BlockEntangledChaliceConfig.staticBlockRendering) {
            EvilCraft._instance.getProxy().registerRenderer(getInstance(), RenderBlockEntityEntangledChalice::new);
        }
    }

}

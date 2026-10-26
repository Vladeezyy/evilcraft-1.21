package org.cyclops.evilcraft.block;

import net.minecraft.world.level.block.Block;
import org.cyclops.cyclopscore.config.ModConfigLocation;
import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.BlockConfig;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for the {@link BlockBloodChest}.
 * @author rubensworks
 *
 */
public class BlockEternalWaterConfig extends BlockConfig {

    @ConfigurablePropertyCommon(category = "block", comment = "If the Eternal Water Block should auto-output water to adjacent blocks by default.", isCommandable = true, configLocation = ModConfigLocation.SERVER)
    public static boolean autoOutputDefault = true;

    public BlockEternalWaterConfig() {
        super(
                EvilCraft._instance,
            "eternal_water",
                eConfig -> new BlockEternalWater(Block.Properties.of()
                        .strength(0.5F)),
                getDefaultItemConstructor(EvilCraft._instance)
        );
    }

}

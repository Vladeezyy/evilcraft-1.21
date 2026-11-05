package org.cyclops.evilcraft.block;

import org.cyclops.evilcraft.fabric.event.ModLifecycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;

/**
 * Config for the Blood Waxed Coal.
 * @author rubensworks
 *
 */
public class BlockBloodWaxedCoalConfig extends BlockConfigCommon<IModBase> {

    public BlockBloodWaxedCoalConfig() {
        super(
            EvilCraft._instance,
            "blood_waxed_coal_block",
                (eConfig, properties) -> new Block(properties
                        .requiresCorrectToolForDrops()
                        .strength(3.0F, 5.0F)
                        .sound(SoundType.METAL)) {
                    
                    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                        return 5;
                    }

                    
                    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                        return 5;
                    }
                },
                getDefaultItemConstructor(EvilCraft._instance)
        );
        ModLifecycle.afterRegistries(() -> net.fabricmc.fabric.api.registry.FuelRegistryEvents.BUILD.register((builder, context) -> builder.add(this.getItemInstance(), 32000)));
    }

}

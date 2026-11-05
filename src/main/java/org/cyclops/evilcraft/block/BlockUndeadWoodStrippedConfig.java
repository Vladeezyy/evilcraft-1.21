package org.cyclops.evilcraft.block;

import org.cyclops.evilcraft.fabric.event.ModLifecycle;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import org.cyclops.evilcraft.fabric.event.BlockEvent;
import org.cyclops.cyclopscore.config.extendedconfig.BlockConfigCommon;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.evilcraft.EvilCraft;
import org.cyclops.evilcraft.RegistryEntries;

/**
 * Config for the Stripped Undead Wood.
 * @author rubensworks
 *
 */
public class BlockUndeadWoodStrippedConfig extends BlockConfigCommon<IModBase> {

    public BlockUndeadWoodStrippedConfig() {
        super(
                EvilCraft._instance,
            "undead_wood_stripped",
                (eConfig, properties) -> new RotatedPillarBlock(properties
                        .mapColor(MapColor.TERRACOTTA_ORANGE)
                        .strength(2.0F, 3.0F)
                        .sound(SoundType.WOOD)) {
                    
                    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                        return 5;
                    }

                    
                    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                        return 20;
                    }
                },
                getDefaultItemConstructor(EvilCraft._instance)
        );
        ModLifecycle.afterRegistries(() -> StrippableBlockRegistry.register(RegistryEntries.BLOCK_UNDEAD_WOOD.get(), RegistryEntries.BLOCK_UNDEAD_WOOD_STRIPPED.get()));
    }


}

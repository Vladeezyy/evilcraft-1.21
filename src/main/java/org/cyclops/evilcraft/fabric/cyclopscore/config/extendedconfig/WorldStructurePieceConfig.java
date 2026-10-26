package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;


import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import org.cyclops.evilcraft.fabric.cyclopscore.config.ConfigurableTypes;
import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.Function;

/**
 * Config for world structures.
 * @author rubensworks
 * @see ExtendedConfigCommon
 */
public abstract class WorldStructurePieceConfig extends ExtendedConfigForge<WorldStructurePieceConfig, StructurePieceType>{
    public WorldStructurePieceConfig(ModBase<?> mod, String namedId, Function<WorldStructurePieceConfig, StructurePieceType> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public String getTranslationKey() {
        return "structure_pieces." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableTypeCommon getConfigurableType() {
        return ConfigurableTypes.WORLD_STRUCTURE_PIECE;
    }

    @Override
    public Registry<? super StructurePieceType> getRegistry() {
        return BuiltInRegistries.STRUCTURE_PIECE;
    }
}

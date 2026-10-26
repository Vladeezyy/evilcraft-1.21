package org.cyclops.evilcraft.core.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.cyclops.evilcraft.fabric.cyclopscore.blockentity.CyclopsBlockEntity;
import org.joml.Vector4f;

/**
 * Base tile entity for beacons.
 * @author immortaleeb
 *
 */
public abstract class BlockEntityBeacon extends CyclopsBlockEntity {

    @Environment(EnvType.CLIENT)
    private Vector4f beamColor;

    private boolean isActive;

    public BlockEntityBeacon(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
        isActive = true;
    }

    /**
     * Get the outer color.
     * @return The outer color.
     */
    @Environment(EnvType.CLIENT)
    public Vector4f getBeamColor() {
        return beamColor;
    }

    /**
     * Set the outer color.
     * @param beamOuterColor The outer color.
     */
    @Environment(EnvType.CLIENT)
    public void setBeamColor(Vector4f beamOuterColor) {
        this.beamColor = beamOuterColor;
    }

    /**
     * If the beam should be rendered.
     * @return If it is active.
     */
    public boolean isBeamActive() {
        return isActive;
    }

    /**
     * Set if the beam should be rendered.
     * @param active If it is active.
     */
    public void setBeamActive(boolean active) {
        this.isActive = active;
    }
}

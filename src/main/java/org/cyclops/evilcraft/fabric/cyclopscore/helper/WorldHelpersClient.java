package org.cyclops.evilcraft.fabric.cyclopscore.helper;



import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

/**
 * @author rubensworks
 */
@Deprecated // TODO: remove in next major version
public class WorldHelpersClient {

    public static Level getActiveLevel() {
        return Minecraft.getInstance().level;
    }

}

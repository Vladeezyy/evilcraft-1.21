package org.cyclops.evilcraft.fabric.cyclopscore.helper;



import net.minecraft.world.level.Level;
import org.cyclops.evilcraft.fabric.ServerLifecycleHooks;

/**
 * @author rubensworks
 */
@Deprecated // TODO: remove in next major version
public class WorldHelpersServer {

    public static Level getActiveLevel() {
        return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
    }

}

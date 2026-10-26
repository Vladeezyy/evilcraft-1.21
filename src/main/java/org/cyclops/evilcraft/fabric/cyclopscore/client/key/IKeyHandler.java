package org.cyclops.evilcraft.fabric.cyclopscore.client.key;



import net.minecraft.client.KeyMapping;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A handler responsible for handling key presses.
 *
 * @author immortaleeb
 *
 */
@Environment(EnvType.CLIENT)
public interface IKeyHandler {

    /**
     * This method is called whenever a key, which is mapped
     * to this KeyHandler is pressed.
     *
     * @param kb {@link KeyMapping} of the key that was pressed.
     */
    public void onKeyPressed(KeyMapping kb);
}

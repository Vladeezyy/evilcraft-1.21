package org.cyclops.evilcraft.fabric.cyclopscore.client.key;

import net.minecraft.client.KeyMapping;
import org.cyclops.cyclopscore.init.IRegistry;

/**
 * Hotkey registry
 * @author rubensworks
 */
public interface IKeyRegistry extends IRegistry {

    /**
     * Handles key presses for keybindings added by the mod, every client tick.
     */
    public void onPlayerKeyInput();

    /**
     * Binds a {@link IKeyHandler} to key presses of the specified {@link KeyMapping}.
     * @param kb {@link KeyMapping} to which we bind the {@link IKeyHandler}.
     * @param handler {@link IKeyHandler} that will be linked to presses of the given {@link KeyMapping}.
     */
    public void addKeyHandler(KeyMapping kb, IKeyHandler handler);

}

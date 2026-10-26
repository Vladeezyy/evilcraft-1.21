package org.cyclops.evilcraft.fabric.cyclopscore.client.key;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

/**
 * Registers key mappings with Fabric.
 */
public class RegisterKeyMappingsEvent {
    public void register(KeyMapping keyMapping) {
        KeyBindingHelper.registerKeyBinding(keyMapping);
    }
}

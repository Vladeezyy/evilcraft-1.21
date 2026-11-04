package org.cyclops.evilcraft.fabric.cyclopscore.client.key;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

/**
 * Registers key mappings with Fabric.
 */
public class RegisterKeyMappingsEvent {
    public void register(KeyMapping keyMapping) {
        KeyMappingHelper.registerKeyMapping(keyMapping);
    }
}

package org.cyclops.evilcraft.fabric.cyclopscore.init;

import java.util.function.Consumer;

/**
 * A mod base; the update checker of Cyclops Core's NeoForge version is not part of the Fabric port.
 * @author rubensworks
 */
public abstract class ModBaseVersionable<T extends ModBaseVersionable<T>> extends ModBase<T> {
    public ModBaseVersionable(String modId, Consumer<T> instanceSetter) {
        super(modId, instanceSetter);
    }
}

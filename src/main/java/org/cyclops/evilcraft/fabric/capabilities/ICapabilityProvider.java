package org.cyclops.evilcraft.fabric.capabilities;

import javax.annotation.Nullable;

/**
 * Provides a capability for an object (block entity, item stack) and a context.
 */
@FunctionalInterface
public interface ICapabilityProvider<O, C, T> {
    @Nullable
    T getCapability(O object, C context);
}

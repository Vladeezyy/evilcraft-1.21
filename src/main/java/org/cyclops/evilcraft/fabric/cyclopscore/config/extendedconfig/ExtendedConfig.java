package org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfigCommon;
import org.cyclops.evilcraft.fabric.cyclopscore.init.ModBase;

import java.util.function.Function;

/**
 * A config without a registry entry.
 * @author rubensworks
 */
public abstract class ExtendedConfig<C extends ExtendedConfig<C, I>, I> extends ExtendedConfigCommon<C, I, ModBase<?>> {
    public ExtendedConfig(ModBase<?> mod, String namedId, Function<C, ? extends I> elementConstructor) {
        super(mod, namedId, elementConstructor);
    }

    @Override
    public ModBase<?> getMod() {
        return super.getMod();
    }
}
